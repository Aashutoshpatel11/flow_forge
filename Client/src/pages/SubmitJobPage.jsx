import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { JOB_TYPES } from "../components/jobs/jobRegistry";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
// Update this path once you share JobController.java
const CREATE_JOB_ENDPOINT = "/api/v1/job/create";

export default function SubmitJobPage() {
    const navigate = useNavigate();
    const jobTypeKeys = Object.keys(JOB_TYPES);
    const [selectedType, setSelectedType] = useState(jobTypeKeys[0]);
    const [serverError, setServerError] = useState("");

    const activeConfig = JOB_TYPES[selectedType];
    const ActiveFieldsComponent = activeConfig.component;

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting },
    } = useForm({
        defaultValues: activeConfig.defaultValues,
    });

    const handleTypeChange = (newType) => {
        setSelectedType(newType);
        setServerError("");
        reset(JOB_TYPES[newType].defaultValues);
    };

    const onSubmit = async (formData) => {
        setServerError("");
        const payload = activeConfig.buildPayload(formData);

        try {
            const response = await fetch(`${API_BASE_URL}${CREATE_JOB_ENDPOINT}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    const errData = await response.json();
                    throw new Error(errData.message || "Failed to submit job.");
                }
                const errText = await response.text();
                throw new Error(errText || "Failed to submit job.");
            }

            navigate("/dashboard", {
                state: { message: `${activeConfig.label} submitted successfully.` },
            });
        } catch (err) {
            setServerError(err.message || "Unable to connect to the server.");
        }
    };

    return (
        <div className="mx-auto max-w-3xl px-4 py-10">
            <div className="mb-6 flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold">Submit New Job</h1>
                    <p className="text-sm opacity-70">{activeConfig.description}</p>
                </div>
                <Link to="/dashboard" className="btn btn-ghost btn-sm">
                    ← Back to Jobs
                </Link>
            </div>

            <div className="card bg-base-100 shadow-xl border border-base-300">
                <div className="card-body">
                    {/* Job Type Selector */}
                    <fieldset className="fieldset mb-2">
                        <legend className="fieldset-legend">Job Type</legend>
                        <select
                            value={selectedType}
                            onChange={(e) => handleTypeChange(e.target.value)}
                            className="select w-full"
                        >
                            {jobTypeKeys.map((typeKey) => (
                                <option key={typeKey} value={typeKey}>
                                    {JOB_TYPES[typeKey].label}
                                </option>
                            ))}
                        </select>
                    </fieldset>

                    <div className="divider my-2" />

                    {serverError && (
                        <div role="alert" className="alert alert-error alert-soft text-sm mb-3">
                            <span>{serverError}</span>
                        </div>
                    )}

                    <form onSubmit={handleSubmit(onSubmit)} noValidate>
                        <ActiveFieldsComponent register={register} errors={errors} />

                        <div className="mt-6 flex justify-end gap-3">
                            <Link to="/dashboard" className="btn btn-ghost">
                                Cancel
                            </Link>
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="btn btn-primary"
                            >
                                {isSubmitting ? (
                                    <>
                                        <span className="loading loading-spinner loading-sm" />
                                        Submitting...
                                    </>
                                ) : (
                                    `Submit ${activeConfig.label}`
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}