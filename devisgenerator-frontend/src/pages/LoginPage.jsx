import { useState, useEffect } from 'react'
import { useNavigate, Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { login } from '../api/apiAuth.js'

export default function LoginPage() {
    const { saveAuth } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const [usernameError, setUsernameError] = useState('')
    const [passwordError, setPasswordError] = useState('')

    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const [successMessage, setSuccessMessage] = useState(
        location.state?.message || null
    )

    //const response = await login(username, password)

    useEffect(() => {

        if (successMessage) {

            const timer = setTimeout(() => {

                setSuccessMessage(null)

                navigate(
                    location.pathname,
                    { replace: true }
                )

            }, 2000)

            return () => clearTimeout(timer)

        }

    }, [successMessage, navigate, location.pathname])

    const validateUsername = () => {

        if (!username.trim()) {
            setUsernameError(
                "Le nom d'utilisateur est obligatoire"
            )
            return false
        }

        setUsernameError('')
        return true

    }

    const validatePassword = () => {

        if (!password.trim()) {
            setPasswordError(
                "Le mot de passe est obligatoire"
            )
            return false
        }

        setPasswordError('')
        return true

    }

    const handleSubmit = async (e) => {

        e.preventDefault()

        const isUsernameValid =
            validateUsername()

        const isPasswordValid =
            validatePassword()

        if (
            !isUsernameValid ||
            !isPasswordValid
        ) {
            return
        }

        setLoading(true)
        setError(null)

        try {
            const response = await login(username, password)

            saveAuth(response.data.token,
                username)

            navigate('/dashboard')

        } catch (err) {

            setError(
                err.response?.data ||
                "Nom d'utilisateur ou mot de passe incorrects"
            )

        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-base-100 flex items-center justify-center">

            <div className="card bg-base-200 w-full max-w-md p-8 rounded-2xl">

                {/* Logo */}
                <div className="flex justify-center mb-6">
                    <div className="w-16 h-16 rounded-full bg-primary"></div>
                </div>

                {/* Titre */}
                <h1 className="text-2xl font-bold text-base-content mb-2 text-center">
                    Connexion à votre espace
                </h1>

                <p className="text-center text-base-content/60 mb-6 text-sm">
                    Entrez vos identifiants
                </p>

                {successMessage && (
                    <div
                        className="mb-4 rounded-lg border px-4 py-3"
                        style={{
                            background: '#052E16',
                            border: '1px solid #166534',
                            color: '#86EFAC'
                        }}
                    >
                        <span>{successMessage}</span>
                    </div>
                )}

                {error && (
                    <div
                        className="mb-4 rounded-lg border px-4 py-3"
                        style={{
                            backgroundColor: '#450A0A',
                            borderColor: '#7F1D1D',
                            color: '#FCA5A5'
                        }}
                    >
                        <span>{error}</span>
                    </div>
                )}

                <form
                    onSubmit={handleSubmit}
                    className="flex flex-col gap-4"
                >
                    <div className="w-full">
                    <input
                        type="text"
                        placeholder="Nom d'utilisateur"
                        className="input input-bordered w-full bg-base-100 rounded-lg"
                        value={username}
                        onChange={(e) => {

                            setUsername(e.target.value)

                            if (usernameError) {
                                setUsernameError('')
                            }

                            setError(null)

                        }}
                        onBlur={validateUsername}
                    />

                    {usernameError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {usernameError}
                        </p>
                    )}
                    </div>

                    <div className="w-full">
                    <input
                        type="password"
                        placeholder="Mot de passe"
                        className="input input-bordered w-full bg-base-100 rounded-lg"
                        value={password}
                        onChange={(e) => {

                            setPassword(e.target.value)

                            if (passwordError) {
                                setPasswordError('')
                            }

                            setError(null)

                        }}
                        onBlur={validatePassword}
                    />

                    {passwordError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {passwordError}
                        </p>
                    )}
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-full mt-2 rounded-lg"
                        disabled={loading}
                    >
                        {loading
                            ? <span className="loading loading-spinner" />
                            : 'Se connecter'
                        }
                    </button>

                </form>

                <p className="text-center text-base-content/60 mt-6 text-sm">
                    Pas encore de compte ?{' '}
                    <Link
                        to="/register"
                        className="text-primary hover:underline"
                    >
                        S'inscrire
                    </Link>
                </p>

            </div>

        </div>
    )
}