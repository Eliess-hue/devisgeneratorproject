import { useState } from "react"

import {
    sendQuoteEmail
} from "../api/apiQuote"

export default function useEmail() {

    const [isSending, setIsSending] =
        useState(false)

    const [error, setError] =
        useState(null)

    const sendEmail = async (id) => {

        setIsSending(true)
        setError(null)

        try {

            await sendQuoteEmail(id)

        } catch (err) {

            console.error(err)

            setError(
                err.response?.data ??
                "Impossible d'envoyer le devis"
            )

            throw err

        } finally {

            setIsSending(false)

        }

    }

    const clearError = () => {

        setError(null)

    }

    return {

        sendEmail,
        isSending,
        error,
        clearError

    }

}