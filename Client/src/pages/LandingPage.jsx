import { Link } from "react-router-dom";
import { useSelector } from "react-redux";

const features = [
    {
        title: "Asynchronous Execution",
        description:
            "Offload heavy email, webhook, and data jobs to Kafka-backed worker pools without blocking your main API threads.",
        badge: "Kafka",
    },
    {
        title: "Exponential Backoff Retries",
        description:
            "Transient failures are retried automatically with configurable backoff intervals so downstream services aren't overwhelmed.",
        badge: "Resilient",
    },
    {
        title: "Dead-Letter Queues (DLQ)",
        description:
            "Jobs that exhaust all retry attempts are safely isolated in a DLQ for inspection, debugging, and one-click replay.",
        badge: "Fault-Tolerant",
    },
    {
        title: "Worker Heartbeat Recovery",
        description:
            "Stalled or crashed workers are detected via heartbeats, automatically reassigning orphaned jobs to healthy nodes.",
        badge: "Self-Healing",
    },
    {
        title: "Redis Rate Limiting",
        description:
            "Protect third-party APIs and internal databases with distributed per-queue and per-tenant Redis rate limits.",
        badge: "Redis",
    },
    {
        title: "Role-Based Access & Security",
        description:
            "Secured end-to-end with dual-token HTTP-only cookie authentication and granular role-based permissions.",
        badge: "Spring Security",
    },
];

export default function LandingPage() {
    const { isAuthenticated } = useSelector((state) => state.auth);

    return (
        <div className="bg-base-100 text-base-content">
            {/* Hero Section */}
            <section className="hero min-h-[75vh] bg-gradient-to-b from-base-200/70 to-base-100 px-4 py-16">
                <div className="hero-content mx-auto max-w-6xl flex-col gap-12 lg:flex-row-reverse">
                    {/* Mock Job Pipeline Preview */}
                    <div className="w-full max-w-lg">
                        <div className="mockup-code bg-neutral text-neutral-content shadow-2xl border border-base-300">
              <pre data-prefix="$">
                <code>POST /api/v1/jobs/create</code>
              </pre>
                            <pre data-prefix=">" className="text-info">
                <code>{`{ "type": "EMAIL", "priority": "HIGH" }`}</code>
              </pre>
                            <pre data-prefix=">" className="text-warning">
                <code>[Kafka] Produced to topic: flowforge.jobs.email</code>
              </pre>
                            <pre data-prefix=">" className="text-success">
                <code>[Worker-02] Heartbeat OK · Executed in 42ms</code>
              </pre>
                            <pre data-prefix="✓" className="bg-success text-success-content">
                <code>Status: COMPLETED (retries: 0)</code>
              </pre>
                        </div>
                    </div>

                    {/* Hero Copy */}
                    <div className="max-w-xl text-center lg:text-left">
                        <div className="badge badge-primary badge-outline mb-4 gap-2 px-3 py-3 font-medium">
                            Distributed Background Job Engine
                        </div>
                        <h1 className="text-4xl font-extrabold tracking-tight sm:text-5xl">
                            Reliable background jobs, <span className="text-primary">engineered for scale.</span>
                        </h1>
                        <p className="py-6 text-base opacity-80 sm:text-lg">
                            FlowForge combines Java 21, Spring Boot, Apache Kafka, and Redis to process asynchronous workloads with automatic retries, dead-letter queues, and worker recovery.
                        </p>
                        <div className="flex flex-wrap justify-center gap-3 lg:justify-start">
                            {isAuthenticated ? (
                                <Link to="/dashboard" className="btn btn-primary btn-lg">
                                    Go to Dashboard
                                </Link>
                            ) : (
                                <>
                                    <Link to="/register" className="btn btn-primary btn-lg">
                                        Get Started Free
                                    </Link>
                                    <Link to="/login" className="btn btn-outline btn-lg">
                                        Sign In
                                    </Link>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section id="features" className="mx-auto max-w-7xl px-6 py-20">
                <div className="mx-auto mb-12 max-w-2xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
                        Built for production workloads
                    </h2>
                    <p className="mt-3 opacity-75">
                        Every primitive you need to queue, monitor, and recover distributed background tasks.
                    </p>
                </div>

                <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                    {features.map((feature) => (
                        <div
                            key={feature.title}
                            className="card bg-base-200/60 border border-base-300 shadow-sm transition hover:border-primary/50 hover:shadow-md"
                        >
                            <div className="card-body">
                                <div className="flex items-center justify-between gap-2">
                                    <h3 className="card-title text-lg">{feature.title}</h3>
                                    <span className="badge badge-soft badge-primary text-xs">
                    {feature.badge}
                  </span>
                                </div>
                                <p className="mt-1 text-sm opacity-80">{feature.description}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {/* Architecture / How It Works */}
            <section id="architecture" className="border-y border-base-300 bg-base-200/40 px-6 py-20">
                <div className="mx-auto max-w-5xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight">How FlowForge Works</h2>
                    <p className="mt-2 opacity-75">
                        From API ingestion to guaranteed execution in four stages.
                    </p>

                    <ul className="steps steps-vertical lg:steps-horizontal mt-12 w-full">
                        <li className="step step-primary">
                            <div className="mt-2 text-left lg:text-center">
                                <p className="font-semibold">1. Submit Job</p>
                                <p className="text-xs opacity-70">Validated via REST API & persisted to PostgreSQL</p>
                            </div>
                        </li>
                        <li className="step step-primary">
                            <div className="mt-2 text-left lg:text-center">
                                <p className="font-semibold">2. Enqueue</p>
                                <p className="text-xs opacity-70">Published idempotently to Apache Kafka</p>
                            </div>
                        </li>
                        <li className="step step-primary">
                            <div className="mt-2 text-left lg:text-center">
                                <p className="font-semibold">3. Execute</p>
                                <p className="text-xs opacity-70">Rate-limited via Redis & run by worker groups</p>
                            </div>
                        </li>
                        <li className="step step-primary">
                            <div className="mt-2 text-left lg:text-center">
                                <p className="font-semibold">4. Recover</p>
                                <p className="text-xs opacity-70">Exponential backoff retries or DLQ isolation</p>
                            </div>
                        </li>
                    </ul>
                </div>
            </section>

            {/* Bottom CTA */}
            <section className="mx-auto max-w-5xl px-6 py-20">
                <div className="card bg-primary text-primary-content shadow-xl">
                    <div className="card-body items-center text-center py-12">
                        <h2 className="card-title text-2xl sm:text-3xl font-bold">
                            Ready to orchestrate your background jobs?
                        </h2>
                        <p className="max-w-xl opacity-90">
                            Create your FlowForge account and start dispatching fault-tolerant jobs in minutes.
                        </p>
                        <div className="card-actions mt-4">
                            <Link
                                to={isAuthenticated ? "/dashboard" : "/register"}
                                className="btn btn-neutral btn-lg"
                            >
                                {isAuthenticated ? "Open Dashboard" : "Create Free Account"}
                            </Link>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}