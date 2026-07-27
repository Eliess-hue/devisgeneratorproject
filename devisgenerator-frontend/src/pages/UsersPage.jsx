import useUsers from "../hooks/useUsers"
import Alert from "../components/common/Alert"
import {useAuth} from "../context/AuthContext.jsx";

export default function UsersPage() {

    const {
        users,
        loading,
        error,
        changeRole
    } = useUsers()

    const { id: currentUserId } = useAuth()

    if (loading) {
        return <span className="loading loading-spinner loading-lg" />
    }

    return (

        <div className="space-y-6">

            <div>

                <h1 className="text-3xl font-bold">
                    Gestion des utilisateurs
                </h1>

                <p className="text-base-content/70 mt-1">
                    Gérez les rôles des utilisateurs.
                </p>

            </div>

            {error && (

                <Alert type="error">

                    {error}

                </Alert>

            )}

            <div className="overflow-x-auto rounded-lg border border-base-300">

                <table className="table">

                    <thead>

                    <tr>

                        <th>Nom d'utilisateur</th>
                        <th>Rôle</th>

                    </tr>

                    </thead>

                    <tbody>

                    {users.map(user => (

                        <tr key={user.id}>

                            <td>

                                {user.username}

                            </td>

                            <td>

                                <select
                                    className="select select-bordered select-sm"
                                    value={user.role}
                                    disabled={user.id === currentUserId}
                                    onChange={(e) =>
                                        changeRole(
                                            user.id,
                                            e.target.value
                                        )
                                    }
                                >

                                    <option value="ROLE_USER">
                                        Utilisateur
                                    </option>

                                    <option value="ROLE_ADMIN">
                                        Administrateur
                                    </option>

                                </select>

                            </td>

                        </tr>

                    ))}

                    </tbody>

                </table>

            </div>

        </div>

    )

}