import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ClientsPage from './pages/ClientsPage'
import QuotesPage from './pages/QuotesPage'
import QuoteDetailPage from './pages/QuoteDetailPage'
import Layout from "./components/Layout.jsx";

function PrivateRoute({ children }) {
    const { token } = useAuth()
    return token ? children : <Navigate to="/login" />
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
                </Route>
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    )
}

export default function App() {
    return (
        <AuthProvider>
            <AppRoutes />
        </AuthProvider>
    )
}