import {useState} from "react"

import {
    searchClients,
    createClient,
    updateClient,
    deleteClient
} from "../api/apiClient"

export default function useClients() {

    const [clients, setClients] = useState([])
    const [loading, setLoading] = useState(true)
    const [isSearching, setIsSearching] = useState(false)
    const [error, setError] = useState(null)

    const [totalPages, setTotalPages] = useState(0)
    const [totalElements, setTotalElements] = useState(0)

    const loadClients = async (filters) => {

        if (!loading) {
            setIsSearching(true)
        }

        try {

            const response = await searchClients(filters)

            setClients(response.data.content)
            setTotalPages(response.data.totalPages)
            setTotalElements(response.data.totalElements)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                err.response?.data ??
                "Impossible de charger les clients"
            )

        } finally {

            setLoading(false)
            setIsSearching(false)

        }

    }

    const saveClient = async (client) => {

        try {

            if (client.id) {

                await updateClient(client.id, client)

            } else {

                await createClient(client)

            }

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                err.response?.data ??
                "Impossible d'enregistrer le client"
            )

            throw err

        }

    }

    const removeClient = async (id) => {

        try {

            await deleteClient(id)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de supprimer le client"
            )

            throw err

        }

    }

    const clearError = () => {

        setError(null)

    }

    return {

        clients,
        loading,
        isSearching,
        error,

        totalPages,
        totalElements,

        loadClients,
        saveClient,
        removeClient,
        clearError

    }

}