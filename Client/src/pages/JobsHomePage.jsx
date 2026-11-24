import { useCallback, useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import JobDetailModal from "../components/jobs/JobDetailModal";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const STATUS_BADGE_MAP = {
    PENDING: "badge-warning",
    QUEUED: "badge-info",
    RUNNING: "badge-info",
    IN_PROGRESS: "badge-info",
    COMPLETED: "badge-success",
    SUCCEEDED: "badge-success",
    FAILED: "badge-error",
    CANCELLED: "badge-ghost",
    DEAD_LETTER: "badge-error badge-outline",
};

const CANCELLABLE_STATUSES = new Set(["PENDING", "QUEUED", "RUNNING", "IN_PROGRESS"]);

const getJobId = (job) => job?.id || job?.jobId || job?.uuid;

const formatDate = (value) => {
    if (!value) return "—";
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString();
};

export default function JobsHomePage() {
    const location = useLocation();
    const [jobs, setJobs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [banner, setBanner] = useState(location.state?.message || "");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [cancellingId, setCancellingId] = useState(null);
    const [selectedJobId, setSelectedJobId] = useState(null);

    const fetchJobs = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/job`, {
                method: "GET",
                credentials: "include",
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch jobs (HTTP ${response.status})`);
            }

            const data = await response.json();
            setJobs(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message || "Unable to load jobs.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchJobs();
    }, [fetchJobs]);

    const handleJobUpdate = useCallback((updatedJob) => {
        const updatedId = getJobId(updatedJob);
        setJobs((prev) =>
            prev.map((j) => (getJobId(j) === updatedId ? updatedJob : j))
        );
    }, []);

    const handleCancelJob = async (jobId) => {
        setCancellingId(jobId);
        setError("");
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/job/cancel/${jobId}`, {
                method: "PATCH",
                credentials: "include",
            });

            if (!response.ok) {
                const errText = await response.text();
                throw new Error(errText || "Failed to cancel job.");
            }

            const result = await response.json();
            setBanner(result?.message || "Job cancelled successfully.");
            await fetchJobs();
        } catch (err) {
            setError(err.message || "Unable to cancel job.");
        } finally {
            setCancellingId(null);
        }
    };

    const uniqueStatuses = [
        "ALL",
        ...Array.from(new Set(jobs.map((j) => j.status).filter(Boolean))),
    ];

    const filteredJobs =
        statusFilter === "ALL"
            ? jobs
            : jobs.filter((job) => job.status === statusFilter);

    return (
        <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6">
            {/* Top Bar */}
            <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight">Submitted Jobs</h1>
                    <p className="text-sm opacity-70">
                        Monitor queued, running, and completed background jobs.
                    </p>
                </div>

                <div className="flex items-center gap-2">
                    <button
                        type="button"
                        onClick={fetchJobs}
                        disabled={loading}
                        className="btn btn-outline btn-sm sm:btn-md"
                    >
                        {loading ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            "Refresh"
                        )}
                    </button>
                    <Link to="/jobs/new" className="btn btn-primary btn-sm sm:btn-md">
                        + Submit New Job
                    </Link>
                </div>
            </div>

            {/* Banners */}
            {banner && (
                <div role="alert" className="alert alert-success alert-soft mb-4 text-sm">
                    <span>{banner}</span>
                    <button
                        type="button"
                        onClick={() => setBanner("")}
                        className="btn btn-ghost btn-xs"
                    >
                        ✕
                    </button>
                </div>
            )}

            {error && (
                <div role="alert" className="alert alert-error alert-soft mb-4 text-sm">
                    <span>{error}</span>
                </div>
            )}

            {/* Status Filter Tabs */}
            {jobs.length > 0 && (
                <div className="mb-4 flex flex-wrap gap-2">
                    {uniqueStatuses.map((status) => (
                        <button
                            key={status}
                            type="button"
                            onClick={() => setStatusFilter(status)}
                            className={`btn btn-xs sm:btn-sm ${
                                statusFilter === status ? "btn-primary" : "btn-ghost"
                            }`}
                        >
                            {status}
                        </button>
                    ))}
                </div>
            )}

            {/* Jobs Table */}
            <div className="card bg-base-100 border border-base-300 shadow-sm">
                <div className="card-body p-0">
                    {loading ? (
                        <div className="flex items-center justify-center py-16">
                            <span className="loading loading-spinner loading-lg text-primary" />
                        </div>
                    ) : filteredJobs.length === 0 ? (
                        <div className="py-16 text-center">
                            <p className="text-base font-medium opacity-80">No jobs found</p>
                            <p className="mt-1 text-sm opacity-60">
                                Submit your first background job to see it tracked here.
                            </p>
                            <Link to="/jobs/new" className="btn btn-primary btn-sm mt-4">
                                Submit New Job
                            </Link>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="table table-zebra">
                                <thead>
                                <tr>
                                    <th>Job ID</th>
                                    <th>Type</th>
                                    <th>Status</th>
                                    <th>Summary</th>
                                    <th>Created</th>
                                    <th className="text-right">Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredJobs.map((job) => {
                                    const jobId = getJobId(job);
                                    const statusKey = String(job.status || "UNKNOWN").toUpperCase();
                                    const badgeClass =
                                        STATUS_BADGE_MAP[statusKey] || "badge-neutral";
                                    const canCancel = CANCELLABLE_STATUSES.has(statusKey);

                                    return (
                                        <tr key={jobId}>
                                            <td className="font-mono text-xs">
                                                {jobId ? `${String(jobId).slice(0, 8)}...` : "—"}
                                            </td>
                                            <td>
                          <span className="badge badge-soft badge-primary badge-sm">
                            {job.type || "JOB"}
                          </span>
                                            </td>
                                            <td>
                          <span className={`badge badge-sm ${badgeClass}`}>
                            {statusKey}
                          </span>
                                            </td>
                                            <td className="max-w-xs truncate text-sm">
                                                {job.subject || job.description || "—"}
                                            </td>
                                            <td className="text-xs opacity-75">
                                                {formatDate(job.createdAt || job.createdDate)}
                                            </td>
                                            <td className="text-right">
                                                <div className="inline-flex gap-2">
                                                    <button
                                                        type="button"
                                                        onClick={() => setSelectedJobId(jobId)}
                                                        className="btn btn-ghost btn-xs"
                                                    >
                                                        View
                                                    </button>
                                                    {canCancel && (
                                                        <button
                                                            type="button"
                                                            disabled={cancellingId === jobId}
                                                            onClick={() => handleCancelJob(jobId)}
                                                            className="btn btn-error btn-outline btn-xs"
                                                        >
                                                            {cancellingId === jobId ? "Cancelling..." : "Cancel"}
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* Live Polling Job Detail Modal */}
            <JobDetailModal
                jobId={selectedJobId}
                onClose={() => setSelectedJobId(null)}
                onJobUpdate={handleJobUpdate}
            />
        </div>
    );
}