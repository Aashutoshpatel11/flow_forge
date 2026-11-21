import {useJobPolling} from "../../hooks/useJobPooling.js";

const STATUS_BADGE_MAP = {
    PENDING: "badge-warning",
    QUEUED: "badge-info",
    RUNNING: "badge-info",
    IN_PROGRESS: "badge-info",
    COMPLETED: "badge-success",
    SUCCESS: "badge-success",
    FAILED: "badge-error",
    CANCELLED: "badge-ghost",
    DEAD_LETTER: "badge-error badge-outline",
};

const RESULT_BADGE_MAP = {
    SUCCESS: "badge-success",
    FAILED: "badge-error",
    FAILURE: "badge-error",
    PENDING: "badge-warning",
};

const formatDate = (value) => {
    if (!value) return "—";
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString();
};

export default function JobDetailModal({ jobId, onClose, onJobUpdate }) {
    const { job, loading, error, isPolling, refetch } = useJobPolling(jobId, {
        intervalMs: 9000,
        onUpdate: onJobUpdate,
    });

    if (!jobId) return null;

    const statusKey = String(job?.status || "PENDING").toUpperCase();
    const resultKey = job?.result ? String(job.result).toUpperCase() : null;
    // Handles both Jackson's default camelCase ("errorMessage") and exact field name ("ErrorMessage")
    const jobErrorMessage = job?.errorMessage || job?.ErrorMessage;

    return (
        <dialog className="modal modal-open">
            <div className="modal-box max-w-2xl">
                {/* Header */}
                <div className="flex items-center justify-between gap-2">
                    <div className="flex flex-wrap items-center gap-2">
                        <h3 className="text-lg font-bold">Job Details</h3>
                        {job?.type && (
                            <span className="badge badge-primary badge-soft badge-sm">
                {job.type}
              </span>
                        )}
                        {isPolling && (
                            <span className="badge badge-info badge-soft badge-sm gap-1.5">
                <span className="loading loading-dots loading-xs" />
                Live
              </span>
                        )}
                    </div>

                    <button
                        type="button"
                        onClick={refetch}
                        disabled={loading}
                        className="btn btn-ghost btn-xs"
                    >
                        Refresh
                    </button>
                </div>

                {/* Fetch Error */}
                {error && (
                    <div role="alert" className="alert alert-error alert-soft mt-4 text-sm">
                        <span>{error}</span>
                    </div>
                )}

                {loading && !job ? (
                    <div className="flex justify-center py-12">
                        <span className="loading loading-spinner loading-md text-primary" />
                    </div>
                ) : job ? (
                    <div className="mt-4 space-y-4">
                        {/* Key Metadata Grid */}
                        <div className="grid grid-cols-2 gap-3 rounded-box border border-base-300 bg-base-200/50 p-4 sm:grid-cols-4">
                            <div>
                                <p className="text-xs opacity-60">Status</p>
                                <span
                                    className={`badge badge-sm mt-1 ${
                                        STATUS_BADGE_MAP[statusKey] || "badge-neutral"
                                    }`}
                                >
                  {statusKey}
                </span>
                            </div>

                            <div>
                                <p className="text-xs opacity-60">Result</p>
                                {resultKey ? (
                                    <span
                                        className={`badge badge-sm mt-1 ${
                                            RESULT_BADGE_MAP[resultKey] || "badge-neutral"
                                        }`}
                                    >
                    {resultKey}
                  </span>
                                ) : (
                                    <p className="mt-1 text-sm font-medium opacity-60">—</p>
                                )}
                            </div>

                            <div>
                                <p className="text-xs opacity-60">Priority</p>
                                <p className="mt-1 text-sm font-semibold">
                                    {job.priority ?? 1}
                                </p>
                            </div>

                            <div>
                                <p className="text-xs opacity-60">Attempts</p>
                                <p className="mt-1 text-sm font-semibold">
                                    {job.attemptCount ?? 0} / {job.maxCount ?? 3}
                                </p>
                            </div>
                        </div>

                        {/* Timestamps */}
                        <div className="grid gap-3 rounded-box border border-base-300 bg-base-200/30 p-4 text-xs sm:grid-cols-3">
                            <div>
                                <p className="opacity-60">Scheduled At</p>
                                <p className="mt-1 font-medium">{formatDate(job.scheduledAt)}</p>
                            </div>
                            <div>
                                <p className="opacity-60">Started At</p>
                                <p className="mt-1 font-medium">{formatDate(job.startedAt)}</p>
                            </div>
                            <div>
                                <p className="opacity-60">Completed At</p>
                                <p className="mt-1 font-medium">{formatDate(job.completedAt)}</p>
                            </div>
                        </div>

                        {/* Error Message (only shown if present) */}
                        {jobErrorMessage && (
                            <div role="alert" className="alert alert-error alert-soft text-xs">
                                <div className="w-full">
                                    <p className="font-semibold">Error Message</p>
                                    <p className="mt-1 break-words font-mono">{jobErrorMessage}</p>
                                </div>
                            </div>
                        )}

                        {/* Raw JSON Payload */}
                        <div>
                            <p className="mb-1.5 text-xs font-semibold uppercase tracking-wider opacity-60">
                                Raw JSON
                            </p>
                            <pre className="max-h-72 overflow-auto rounded-box border border-base-300 bg-base-200 p-4 text-xs">
                {JSON.stringify(job, null, 2)}
              </pre>
                        </div>
                    </div>
                ) : null}

                <div className="modal-action">
                    <button type="button" onClick={onClose} className="btn">
                        Close
                    </button>
                </div>
            </div>
            <div className="modal-backdrop" onClick={onClose} />
        </dialog>
    );
}