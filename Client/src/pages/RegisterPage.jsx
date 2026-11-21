import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function RegisterPage() {
    const navigate = useNavigate();
    const [serverError, setServerError] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm({
        defaultValues: {
            name: "",
            email: "",
            password: "",
        },
    });

    const onSubmit = async (data) => {
        setServerError("");
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/auth/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(data),
            });

            if (!response.ok) {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    const errData = await response.json();
                    throw new Error(errData.message || "Registration failed.");
                }
                const errText = await response.text();
                throw new Error(errText || "Registration failed.");
            }

            navigate("/login", {
                state: { message: "Account created successfully. Please log in." },
            });
        } catch (err) {
            setServerError(err.message || "Unable to connect to the server.");
        }
    };

    return (
        <div className="flex min-h-[calc(100vh-8rem)] items-center justify-center bg-base-200/50 px-4 py-12">
            <div className="card w-full max-w-md bg-base-100 shadow-xl border border-base-300">
                <div className="card-body">
                    <div className="mb-2 text-center">
                        <h1 className="text-2xl font-bold">Create your account</h1>
                        <p className="mt-1 text-sm opacity-70">
                            Start managing distributed jobs with FlowForge
                        </p>
                    </div>

                    {serverError && (
                        <div role="alert" className="alert alert-error alert-soft text-sm">
                            <span>{serverError}</span>
                        </div>
                    )}

                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3" noValidate>
                        {/* Name */}
                        <fieldset className="fieldset">
                            <legend className="fieldset-legend">Full Name</legend>
                            <input
                                type="text"
                                placeholder="Aashutosh Patel"
                                autoComplete="name"
                                className={`input w-full ${errors.name ? "input-error" : ""}`}
                                {...register("name", {
                                    required: "Name should be present",
                                    validate: (v) => v.trim().length >= 1 || "Name should be present",
                                })}
                            />
                            {errors.name && (
                                <p className="label text-error">{errors.name.message}</p>
                            )}
                        </fieldset>

                        {/* Email */}
                        <fieldset className="fieldset">
                            <legend className="fieldset-legend">Email</legend>
                            <input
                                type="email"
                                placeholder="you@example.com"
                                autoComplete="email"
                                className={`input w-full ${errors.email ? "input-error" : ""}`}
                                {...register("email", {
                                    required: "Email is required",
                                    pattern: {
                                        value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                                        message: "Invalid email",
                                    },
                                })}
                            />
                            {errors.email && (
                                <p className="label text-error">{errors.email.message}</p>
                            )}
                        </fieldset>

                        {/* Password */}
                        <fieldset className="fieldset">
                            <legend className="fieldset-legend">Password</legend>
                            <div className="join w-full">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="Minimum 8 characters"
                                    autoComplete="new-password"
                                    className={`input join-item w-full ${errors.password ? "input-error" : ""}`}
                                    {...register("password", {
                                        required: "Password is required",
                                        minLength: {
                                            value: 8,
                                            message: "Password should be at least 8 characters",
                                        },
                                    })}
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword((prev) => !prev)}
                                    className="btn btn-neutral btn-outline join-item"
                                >
                                    {showPassword ? "Hide" : "Show"}
                                </button>
                            </div>
                            {errors.password && (
                                <p className="label text-error">{errors.password.message}</p>
                            )}
                        </fieldset>

                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="btn btn-primary mt-4 w-full"
                        >
                            {isSubmitting ? (
                                <>
                                    <span className="loading loading-spinner loading-sm" />
                                    Creating account...
                                </>
                            ) : (
                                "Create Account"
                            )}
                        </button>
                    </form>

                    <p className="mt-4 text-center text-sm opacity-80">
                        Already have an account?{" "}
                        <Link to="/login" className="link link-primary font-medium">
                            Log in
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}