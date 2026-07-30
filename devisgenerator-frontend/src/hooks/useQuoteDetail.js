import { useEffect, useState } from "react";

import {
    getQuoteById,
    addQuoteLine,
    updateQuoteLine,
    deleteQuoteLine,
    duplicateQuote,
    updateQuote
} from "../api/apiQuote";

export default function useQuoteDetail(id) {

    const [quote, setQuote] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loadQuote = async () => {

        setLoading(true);

        try {

            const response = await getQuoteById(id);

            setQuote(response.data);

            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                "Impossible de charger le devis"
            );

            throw err;

        } finally {

            setLoading(false);

        }

    };

    const saveLine = async ({
                                lineId,
                                description,
                                quantity,
                                unitPrice,
                                vatRate
                            }) => {

        const payload = {
            description,
            quantity,
            unitPrice,
            vatRate
        };

        try {

            if (lineId) {

                await updateQuoteLine(
                    id,
                    lineId,
                    payload
                );

            } else {

                await addQuoteLine(
                    id,
                    payload
                );

            }

            await loadQuote();

            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                lineId
                    ? "Impossible de modifier la ligne"
                    : "Impossible d'ajouter la ligne"
            );

            throw err;

        }

    };

    const removeLine = async (lineId) => {

        try {

            await deleteQuoteLine(
                id,
                lineId
            );

            await loadQuote();

            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                "Impossible de supprimer la ligne"
            );

            throw err;

        }

    };

    const duplicate = async () => {

        try {

            const response =
                await duplicateQuote(id);

            setError(null);

            return response.data.id;

        } catch (err) {

            console.error(err);

            setError(
                "Impossible de dupliquer le devis"
            );

            throw err;

        }

    };

    const updateStatus = async (newStatus) => {

        try {

            await updateQuote(
                id,
                quote.client.id,
                newStatus
            );

            await loadQuote();

            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                "Impossible de modifier le statut"
            );

            throw err;

        }

    };

    const clearError = () => {

        setError(null);

    };

    useEffect(() => {

        loadQuote();

    }, [id]);

    return {

        quote,
        loading,
        error,

        loadQuote,
        saveLine,
        removeLine,
        updateStatus,
        duplicate,
        clearError

    };

}