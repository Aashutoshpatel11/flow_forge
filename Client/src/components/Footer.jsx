import { Link } from "react-router-dom";

export default function Footer() {
    return (
        <footer className="border-t border-base-300 bg-base-200 text-base-content">
            <div className="footer sm:footer-horizontal mx-auto max-w-7xl px-6 py-12">
                <aside className="max-w-xs">
                    <Link to="/" className="flex items-center gap-2 text-lg font-bold">
            <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-content">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2">
                <path strokeLinecap="round" strokeLinejoin="round" d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
              </svg>
            </span>
                        <span>FlowForge</span>
                    </Link>
                    <p className="mt-2 text-sm opacity-75">
                        Distributed background job processing with Kafka, Redis, and Spring Boot. Built for reliable async execution, retries, and dead-letter queues.
                    </p>
                </aside>

                <nav>
                    <h6 className="footer-title">Platform</h6>
                    <a href="/#features" className="link link-hover">Features</a>
                    <a href="/#architecture" className="link link-hover">Architecture</a>
                    <Link to="/jobs" className="link link-hover">Job Queue</Link>
                </nav>

                <nav>
                    <h6 className="footer-title">Account</h6>
                    <Link to="/login" className="link link-hover">Log in</Link>
                    <Link to="/register" className="link link-hover">Create Account</Link>
                    <Link to="/dashboard" className="link link-hover">Dashboard</Link>
                </nav>

                <nav>
                    <h6 className="footer-title">Stack</h6>
                    <span className="text-sm opacity-75">Java 21 & Spring Boot</span>
                    <span className="text-sm opacity-75">Apache Kafka & Redis</span>
                    <span className="text-sm opacity-75">PostgreSQL & React</span>
                </nav>
            </div>

            <div className="border-t border-base-300">
                <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-2 px-6 py-4 text-xs opacity-70 sm:flex-row">
                    <p>© {new Date().getFullYear()} FlowForge. All rights reserved.</p>
                    <p>Distributed Job Processing Engine</p>
                </div>
            </div>
        </footer>
    );
}