import { useCallback, useEffect, useRef, useState } from "react";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const TERMINAL_STATUSES = new Set([
    "COMPLETED",
    "SUCCESS",
    "FAILED",
    "CANCELLED",
    "DEAD_LETTER",
]);

export function useJobPolling(jobId, { intervalMs = 9000, onUpdate } = {}) {
    const [job, setJob] = useState(null);
    const [loading, setLoading] = useState(Boolean(jobId));
    const [error, setError] = useState("");
    const [isPolling, setIsPolling] = useState(false);

    const timerRef = useRef(null);
    const abortRef = useRef(null);
    const onUpdateRef = useRef(onUpdate);
    onUpdateRef.current = onUpdate;

    const stopPolling = useCallback(() => {
        if (timerRef.current) {
            clearTimeout(timerRef.current);
            timerRef.current = null;
        }
        if (abortRef.current) {
            abortRef.current.abort();
            abortRef.current = null;
        }
        setIsPolling(false);
    }, []);

    const fetchJob = useCallback(async () => {
        if (!jobId) return;

        abortRef.current = new AbortController();

        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/job/${jobId}`, {
                method: "GET",
                credentials: "include",
                signal: abortRef.current.signal,
            });

            if (!response.ok) {
                let errorMessage = `Failed to fetch job (HTTP ${response.status})`;

                let resp = await response.json();

                if( Array.isArray(resp.errors) && resp.errors.length > 0 ){
                    errorMessage += `${resp.errors.join(', ')}`
                }
                throw new Error(errorMessage);
            }

            const data = await response.json();
            setJob(data);
            setError("");
            setLoading(false);

            if (onUpdateRef.current) {
                onUpdateRef.current(data);
            }

            const status = String(data?.status || "").toUpperCase();
            if (TERMINAL_STATUSES.has(status)) {
                setIsPolling(false);
            } else {
                setIsPolling(true);
                timerRef.current = setTimeout(fetchJob, intervalMs);
            }
        } catch (err) {
            if (err.name === "AbortError") return;
            setError(err.message || "Unable to fetch job status.");
            setLoading(false);
            setIsPolling(false);
        }
    }, [jobId, intervalMs]);

    useEffect(() => {
        if (!jobId) {
            stopPolling();
            setJob(null);
            return;
        }

        setLoading(true);
        setError("");
        fetchJob();

        return () => stopPolling();
    }, [jobId, fetchJob, stopPolling]);

    return { job, loading, error, isPolling, refetch: fetchJob };
}