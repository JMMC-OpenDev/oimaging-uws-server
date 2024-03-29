package fr.jmmc.oimaging.uws;

import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.jmcs.util.runner.LocalLauncher;
import fr.jmmc.jmcs.util.runner.RootContext;
import fr.jmmc.jmcs.util.runner.RunContext;
import fr.jmmc.jmcs.util.runner.process.ProcessContext;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uws.UWSException;
import uws.job.ErrorType;
import uws.job.JobThread;
import uws.job.Result;
import uws.job.UWSJob;
import uws.service.request.UploadFile;

public class OImagingWork extends JobThread {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(OImagingWork.class.getName());
    /** application identifier for LocalService */
    public final static String APP_NAME = "OImaging-uws-worker";
    /** user for LocalService */
    public final static String USER_NAME = "JMMC";
    /** task identifier for LocalService */
    public final static String TASK_NAME = "LocalRunner";
    public static final String INPUTFILE = "inputfile";
    public static final String SOFTWARE = "software";
    public static final String CLI_OPTIONS = "cliOptions";
    /** special exit code indicating the job was cancelled / killed */
    public static final int EXEC_KILLED = 257;

    public OImagingWork(UWSJob j) throws UWSException {
        super(j);
    }

    @Override
    protected void jobWork() throws UWSException, InterruptedException {

        // If the task has been canceled/interrupted, throw the corresponding exception:
        if (isInterrupted()) {
            throw new InterruptedException();
        }

        // Get job Id:
        final String jobId = getJob().getJobId();

        // Check software param
        final String software = (String) getJob().getAdditionalParameterValue(SOFTWARE);

        if (software == null) {
            logger.error(SOFTWARE + " is null");
            throw new UWSException(UWSException.BAD_REQUEST, "Wrong \"" + SOFTWARE + "\" param. An program name is expected!", ErrorType.FATAL);
        }

        // Check software options for cli
        final String cliOptions = (String) getJob().getAdditionalParameterValue(CLI_OPTIONS);

        // Check inputFile param
        final UploadFile inputFile = (UploadFile) getJob().getAdditionalParameterValue(INPUTFILE);

        if (inputFile == null) {
            logger.error(INPUTFILE + "is null");
            throw new UWSException(UWSException.BAD_REQUEST, "Wrong \"" + INPUTFILE + "\" param. An OIFits file is expected!", ErrorType.FATAL);
        }

        File outputFile = null;
        File logFile = null;
        try {
            // prepare the result:
            final Result outputResult = createResult("outputfile");
            final Result logResult = createResult("logfile");

            // and fakes other files using inputfile name
            // Warning : we use the path from getLocation()
            final String inputFilePath = inputFile.getLocation().replaceFirst("file:", "");

            // TODO: create a temporary folder per job (to ensure minimal program isolation ...)
            final String workDir = new File(inputFilePath).getParentFile().getAbsolutePath();
            outputFile = new File(inputFilePath + ".out");
            logFile = new File(inputFilePath + ".log");

            logger.info("Job[{}] submitting task [software: {} inputFile: {}]", jobId, software, inputFilePath);

            OImagingUwsStats.INSTANCE.start(software);

            final int statusCode = exec(software, cliOptions, workDir, inputFilePath, outputFile.getAbsolutePath(), logFile.getAbsolutePath());

            logger.info("Job[{}] exec returned: {}", jobId, statusCode);

            if (statusCode == EXEC_KILLED) {
                OImagingUwsStats.INSTANCE.cancel(software);
                // Interrupt the thread to make JobThread abort this job:
                Thread.currentThread().interrupt();
            } else {
                // TODO: if error, maybe use ErrorSummary but there is no way to return log file ?
                // setError(new ErrorSummary(String msg, ErrorType errorType, String detailedMsgURI));
                if (outputFile.exists()) {
                    OImagingUwsStats.INSTANCE.success(software);

                    FileUtils.saveFile(outputFile, getResultOutput(outputResult));
                    publishResult(outputResult);
                } else {
                    OImagingUwsStats.INSTANCE.error(software);
                }
                if (logFile.exists()) {
                    FileUtils.saveFile(logFile, getResultOutput(logResult));
                    publishResult(logResult);
                }
            }
        } catch (IOException e) {
            // If there is an error, encapsulate it in an UWSException so that an error summary can be published:
            throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, e, "Impossible to write the result file of the Job " + jobId + " !", ErrorType.TRANSIENT);
        } finally {
            if (outputFile != null) {
                outputFile.delete();
            }
            if (logFile != null) {
                logFile.delete();
            }
        }
    }

    /**
     * Launch the given application in background.
     *
     * @param appName
     * @param cliOptions software options on command line or null
     * @param workDir
     * @param inputFilename
     * @param outputFilename
     * @param logFilename
     * @return status code of the executed command
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public int exec(final String appName,
                    final String cliOptions,
                    final String workDir,
                    final String inputFilename,
                    final String outputFilename,
                    final String logFilename) throws IllegalStateException {

        if (StringUtils.isEmpty(appName)) {
            throw new IllegalArgumentException("empty application name !");
        }
        if (StringUtils.isEmpty(inputFilename)) {
            throw new IllegalArgumentException("empty input filename !");
        }
        if (StringUtils.isEmpty(outputFilename)) {
            throw new IllegalArgumentException("empty output filename !");
        }
        if (StringUtils.isEmpty(logFilename)) {
            throw new IllegalArgumentException("empty log filename !");
        }

        // Get job Id:
        final String jobId = getJob().getJobId();

        final RootContext jobContext = LocalLauncher.prepareMainJob(APP_NAME, USER_NAME, workDir, logFilename);

        final String[] cmd;
        if (cliOptions == null) {
            cmd = new String[]{appName, inputFilename, outputFilename};
        } else {
            cmd = new String[]{appName, cliOptions, inputFilename, outputFilename};
        }

        final RunContext runCtx = LocalLauncher.prepareChildJob(jobContext, TASK_NAME, cmd);

        // If the task has been cancelled/interrupted, do not fork process:
        if (Thread.currentThread().isInterrupted()) {
            return EXEC_KILLED;
        }

        final long start = System.nanoTime();

        // Puts the job in the job queue (can throw IllegalStateException if job not queued)
        LocalLauncher.startJob(jobContext);

        // Wait for process completion
        try {
            // TODO: use timeout to kill (too long or hanging) jobs anyway ?
            // Wait for task to be done :
            jobContext.getFuture().get();
        } catch (InterruptedException ie) {
            logger.debug("Job[{}] waitFor: interrupted, killing {}", jobId, jobContext.getId());

            LocalLauncher.cancelOrKillJob(jobContext.getId());

            try {
                // Wait for process to die:
                jobContext.getFuture().get();
            } catch (InterruptedException ie2) {
                logger.debug("Job[{}] waitFor: interrupted 2 {}", jobId);
            } catch (ExecutionException ee) {
                logger.debug("Job[{}] waitFor: execution error", jobId, ee);
            }
            logger.debug("Job[{}] waitFor: interrupted, done", jobId);

        } catch (ExecutionException ee) {
            logger.info("Job[{}] waitFor: execution error", jobId, ee);
        } finally {
            logger.info("Job[{}] finished {} - duration = {} ms.", jobId, jobContext.getState(),
                    1e-6d * (System.nanoTime() - start));
        }

        // retrieve command execution status code
        switch (jobContext.getState()) {
            case STATE_FINISHED_OK:
                return 0;
            case STATE_CANCELED:
            case STATE_INTERRUPTED:
            case STATE_KILLED:
                return EXEC_KILLED;
            default:
                if (runCtx instanceof ProcessContext) {
                    return ((ProcessContext) runCtx).getExitCode();
                }
                return -1;
        }
    }

}
