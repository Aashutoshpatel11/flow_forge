import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import ProtectedRoute from "./components/ProtectedRoute";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import JobsHomePage from "./pages/JobsHomePage";
import SubmitJobPage from "./pages/SubmitJobPage";

export default function App() {
    return (
        <BrowserRouter>
            <div className="flex min-h-screen flex-col">
                <Header />
                <main className="flex-1">
                    <Routes>
                        <Route path="/" element={<LandingPage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/register" element={<RegisterPage />} />

                        <Route
                            path="/dashboard"
                            element={
                                <ProtectedRoute>
                                    <JobsHomePage />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/jobs"
                            element={
                                <ProtectedRoute>
                                    <JobsHomePage />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/jobs/new"
                            element={
                                <ProtectedRoute>
                                    <SubmitJobPage />
                                </ProtectedRoute>
                            }
                        />

                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </main>
                <Footer />
            </div>
        </BrowserRouter>
    );
}