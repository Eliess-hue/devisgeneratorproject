import { useState } from "react"

import {
    searchQuotes,
    createQuote,
    updateQuote,
    deleteQuote,
    duplicateQuote
} from "../api/apiQuote"

export default function useQuotes() {

    const [quotes, setQuotes] = useState([])
    const [loading, setLoading] = useState(true)
    const [isSearching, setIsSearching] = useState(false)
    const [error, setError] = useState(null)

    const [totalPages, setTotalPages] = useState(0)
    const [totalElements, setTotalElements] = useState(0)

    const loadQuotes = async (filters) => {

        if (!loading) {
            setIsSearching(true)
        }

        try {

            const response = await searchQuotes(filters)

            setQuotes(response.data.content)
            setTotalPages(response.data.totalPages)
            setTotalElements(response.data.totalElements)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de charger les devis"
            )

            throw err

        } finally {

            setLoading(false)
            setIsSearching(false)

        }

    }

    const saveQuote = async ({
                                 id,
                                 clientId,
                                 status
                             }) => {

        try {

            if (id) {

                await updateQuote(id, clientId, status)

            } else {

                await createQuote(clientId, status)

            }

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible d'enregistrer le devis"
            )

            throw err

        }

    }

    const removeQuote = async (id) => {

        try {

            await deleteQuote(id)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de supprimer le devis"
            );

            throw err

        }

    }

    const duplicateQuoteById = async (id) => {

        try {

            await duplicateQuote(id)

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de dupliquer le devis"
            )

            throw err

        }

    }

    const clearError = () => {

        setError(null)

    }

    return {

        quotes,
        loading,
        isSearching,
        error,
        totalPages,
        totalElements,

        loadQuotes,
        saveQuote,
        removeQuote,
        duplicateQuote: duplicateQuoteById,
        clearError

    }

}