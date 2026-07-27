import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import {PageTitleProvider} from "./context/PageTitleContext.jsx";
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ClientsPage from './pages/ClientsPage'
import QuotesPage from './pages/QuotesPage'
import QuoteDetailPage from './pages/QuoteDetailPage'
import Layout from "./components/Layout.jsx"
import UsersPage from './pages/UsersPage'

function PrivateRoute({ children }) {

    const {
        token,
        isLoading
    } = useAuth()

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <span className="loading loading-spinner loading-lg" />
            </div>
        )
    }

    return token
        ? children
        : <Navigate to="/login" />;
}

function AdminRoute({ children }) {

    const {
        token,
        role,
        isLoading
    } = useAuth()

    if (isLoading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <span className="loading loading-spinner loading-lg" />
            </div>
        )
    }

    if (!token) {
        return <Navigate to="/login" replace />
    }

    if (role !== 'ROLE_ADMIN') {
        return <Navigate to="/dashboard" replace />
    }

    return children
}

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route element={<PrivateRoute><Layout /></PrivateRoute>}>
                    <Route path="/dashboard" element={<DashboardPage />} />
                    <Route path="/clients" element={<ClientsPage />} />
                    <Route path="/quotes" element={<QuotesPage />} />
                    <Route path="/quotes/:id" element={<QuoteDetailPage />} />
                    <Route
                        path="/users"
                        element={
                            <AdminRoute>
                                <UsersPage />
                            </AdminRoute>
                        }
                    />
                </Route>
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    )
}

export default function App() {
    return (
        <AuthProvider>
            <PageTitleProvider>
                <AppRoutes />
            </PageTitleProvider>
        </AuthProvider>
    )
}