import { useState } from "react"

import { getQuotePdf } from "../api/apiQuote"

export default function usePdf() {

    const [error, setError] = useState(null)


    const openPdf = async (id) => {

        setError(null)

        const pdfWindow = window.open(
            "",
            "_blank"
        )


        if (!pdfWindow) {

            setError(
                "Impossible d'ouvrir le PDF. Veuillez autoriser les popups."
            )

            return

        }


        try {

            const response =
                await getQuotePdf(id)


            const url =
                URL.createObjectURL(
                    response.data
                )


            pdfWindow.location.href = url


            setTimeout(() => {

                URL.revokeObjectURL(url)

            }, 10000)


        } catch (err) {

            console.error(err)

            pdfWindow.close()

            setError(
                "Impossible de générer le PDF"
            )

        }

    }


    const clearError = () => {

        setError(null)

    }


    return {

        openPdf,
        error,
        clearError

    }

}