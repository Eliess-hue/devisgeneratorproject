import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../api/apiAuth.js'

export default function RegisterPage() {

    const navigate = useNavigate()

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const [usernameError, setUsernameError] = useState('')
    const [passwordError, setPasswordError] = useState('')

    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)

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

        if (password.length < 8) {
            setPasswordError(
                "Le mot de passe doit contenir au moins 8 caractères"
            )
            return false
        }

        setPasswordError('')
        return true

    }

    const handleSubmit = async (e) => {

        e.preventDefault()

        const isUsernameValid = validateUsername()
        const isPasswordValid = validatePassword()

        if (!isUsernameValid || !isPasswordValid) {
            return
        }

        setLoading(true)
        setError(null)

        try {

            await register(
                username,
                password
            )

            navigate('/login', {
                state: {
                    message: 'Compte créé avec succès'
                }
            })

        } catch (err) {

            setError(
                err.response?.data ||
                "Impossible de créer le compte"
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
                    Créer un compte
                </h1>

                <p className="text-center text-base-content/60 mb-6 text-sm">
                    Inscrivez-vous pour accéder à votre espace
                </p>

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
                            : "S'inscrire"
                        }
                    </button>

                </form>

                <p className="text-center text-base-content/60 mt-6 text-sm">
                    Déjà un compte ?{' '}
                    <Link
                        to="/login"
                        className="text-primary hover:underline"
                    >
                        Se connecter
                    </Link>
                </p>

            </div>

        </div>
    )
}