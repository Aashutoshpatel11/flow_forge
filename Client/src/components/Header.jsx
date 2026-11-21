import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../redux/slices/authSlice";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:7004";

export default function Header() {
    const { isAuthenticated, user } = useSelector((state) => state.auth);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [isLoggingOut, setIsLoggingOut] = useState(false);

    const handleLogout = async () => {
        setIsLoggingOut(true);
        try {
            await fetch(`${API_BASE_URL}/api/v1/auth/logout`, {
                method: "GET",
                credentials: "include",
            });
        } catch (error) {
            console.error("Logout request failed:", error);
        } finally {
            dispatch(logout());
            setIsLoggingOut(false);
            navigate("/login");
        }
    };

    const userInitial = (user?.name || user?.email || "U").charAt(0).toUpperCase();

    return (
        <header className="sticky top-0 z-50 border-b border-base-300 bg-base-100/90 backdrop-blur">
            <div className="navbar mx-auto max-w-7xl px-4">
                {/* Left: Mobile Menu + Brand */}
                <div className="navbar-start">
                    <div className="dropdown">
                        <div tabIndex={0} role="button" className="btn btn-ghost lg:hidden" aria-label="Open menu">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h8m-8 6h16" />
                            </svg>
                        </div>
                        <ul
                            tabIndex={0}
                            className="menu menu-sm dropdown-content bg-base-200 rounded-box z-50 mt-3 w-52 p-2 shadow-lg border border-base-300"
                        >
                            {isAuthenticated ? (
                                <>
                                    <li><Link to="/dashboard">Dashboard</Link></li>
                                    <li><Link to="/jobs">Jobs</Link></li>
                                </>
                            ) : (
                                <>
                                    <li><a href="/#features">Features</a></li>
                                    <li><a href="/#architecture">Architecture</a></li>
                                    <li className="mt-1 border-t border-base-300 pt-1">
                                        <Link to="/login">Log in</Link>
                                    </li>
                                    <li>
                                        <Link to="/register" className="text-primary font-semibold">Get Started</Link>
                                    </li>
                                </>
                            )}
                        </ul>
                    </div>

                    <Link to="/" className="btn btn-ghost gap-2 px-2 text-xl font-bold tracking-tight">
            <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-content">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2">
                <path strokeLinecap="round" strokeLinejoin="round" d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
              </svg>
            </span>
                        <span>FlowForge</span>
                    </Link>
                </div>

                {/* Center: Desktop Nav Links */}
                <div className="navbar-center hidden lg:flex">
                    <ul className="menu menu-horizontal gap-1 px-1 font-medium">
                        {isAuthenticated ? (
                            <>
                                <li><Link to="/dashboard">Dashboard</Link></li>
                                <li><Link to="/jobs">Jobs</Link></li>
                            </>
                        ) : (
                            <>
                                <li><a href="/#features">Features</a></li>
                                <li><a href="/#architecture">Architecture</a></li>
                            </>
                        )}
                    </ul>
                </div>

                {/* Right: Auth Actions */}
                <div className="navbar-end gap-2">
                    {isAuthenticated ? (
                        <div className="dropdown dropdown-end">
                            <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar avatar-placeholder">
                                <div className="bg-primary text-primary-content w-9 rounded-full">
                                    <span className="text-sm font-semibold">{userInitial}</span>
                                </div>
                            </div>
                            <ul
                                tabIndex={0}
                                className="menu menu-sm dropdown-content bg-base-200 rounded-box z-50 mt-3 w-56 p-2 shadow-lg border border-base-300"
                            >
                                <li className="menu-title px-3 py-1.5">
                  <span className="truncate text-base-content font-semibold">
                    {user?.name || "FlowForge User"}
                  </span>
                                    {user?.email && (
                                        <span className="truncate text-xs font-normal opacity-70">{user.email}</span>
                                    )}
                                </li>
                                <li><Link to="/dashboard">Dashboard</Link></li>
                                <li><Link to="/jobs">Manage Jobs</Link></li>
                                <li className="mt-1 border-t border-base-300 pt-1">
                                    <button
                                        type="button"
                                        onClick={handleLogout}
                                        disabled={isLoggingOut}
                                        className="text-error hover:bg-error/10"
                                    >
                                        {isLoggingOut ? "Logging out..." : "Logout"}
                                    </button>
                                </li>
                            </ul>
                        </div>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-ghost btn-sm sm:btn-md">
                                Log in
                            </Link>
                            <Link to="/register" className="btn btn-primary btn-sm sm:btn-md">
                                Get Started
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}