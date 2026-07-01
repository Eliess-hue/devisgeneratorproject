import { useEffect, useState } from "react"

import { getDashboard } from "../api/apiDashboard"

export default function useDashboard() {

    const [dashboard, setDashboard] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const loadDashboard = async () => {

        setLoading(true)

        try {

            const response = await getDashboard()

            setDashboard(response.data)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de charger le tableau de bord"
            )

            throw err

        } finally {

            setLoading(false)

        }

    }

    const clearError = () => {

        setError(null)

    }

    useEffect(() => {

        loadDashboard()

    }, [])

    return {

        dashboard,
        loading,
        error,

        loadDashboard,
        clearError

    }

}