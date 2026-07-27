import { useEffect, useState } from "react"
import {
    getUsers,
    changeRole as updateUserRole
} from "../api/apiUsers.js"

export default function useUsers() {

    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const fetchUsers = async () => {

        try {

            const response = await getUsers()

            setUsers(response.data)

        } catch (err) {

            setError(
                err.response?.data ||
                "Erreur lors du chargement des utilisateurs."
            )

        } finally {

            setLoading(false)

        }

    }

    useEffect(() => {

        fetchUsers()

    }, [])

    const changeRole = async (id, role) => {

        try {

            const response = await updateUserRole(id, role);

            setUsers(users =>
                users.map(user =>
                    user.id === id
                        ? response.data
                        : user
                )
            )

        } catch (err) {

            setError(
                err.response?.data ||
                "Erreur lors du changement de rôle."
            )

        }

    }

    return {

        users,
        loading,
        error,
        fetchUsers,
        changeRole

    }

}