import {useState, useEffect} from "react"

import {
    getClients,
    createClient,
    updateClient,
    deleteClient
} from "../api/apiClient"

export default function useClients() {

    const [clients, setClients] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const loadClients = async () => {

        setLoading(true);

        try {

            const response = await getClients();

            setClients(response.data);
            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                err.response?.data ??
                "Impossible de charger les clients"
            );

        } finally {

            setLoading(false);

        }

    }

    const saveClient = async ({ id, name, email, phone, address }) => {

        try {

            if (id) {

                await updateClient(id, {
                    name,
                    email,
                    phone,
                    address
                });

            } else {

                await createClient({
                    name,
                    email,
                    phone,
                    address
                });

            }

            await loadClients();

            setError(null);

        } catch (err) {

            console.error(err);

            setError(
                err.response?.data ??
                "Impossible d'enregistrer le client"
            );

            throw err;

        }

    }

    const removeClient = async (id) => {

        try {

            await deleteClient(id);

            await loadClients();

        } catch (err) {

            console.error(err);

            setError(
                "Impossible de supprimer le client"
            );

            throw err;

        }

    }

    const clearError = () => {

        setError(null);

    }

    useEffect(() => {

        loadClients();

    }, []);

    return {
        clients,
        loading,
        error,
        clearError,
        loadClients,
        saveClient,
        removeClient
    };

}