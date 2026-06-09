import {useState, useEffect} from "react";
import {
    getQuotes,
    createQuote,
    updateQuote,
    deleteQuote
} from "../api/apiQuote.js";

import {getClients} from "../api/apiClient.js";
import QuoteModal from "../components/quotes/QuoteModal.jsx";
import QuoteTable from "../components/quotes/QuoteTable.jsx";

export default function QuotesPage() {

    const [quotes, setQuotes] = useState([])

    const [clients, setClients] = useState([])

    const [isModalOpen, setIsModalOpen] = useState(false)

    const [editingQuote, setEditingQuote] = useState(null)

    const [clientId, setClientId] = useState('')
    const [status, setStatus] = useState('draft')

    const [search, setSearch] = useState('')

    const [error, setError] = useState(null)

    const handleNewQuote = () => {
        if (clients.length === 0) {

            setError(
                "Vous devez créer un client avant de créer un devis."
            )

            return

        }

        setEditingQuote(null)

        setClientId('')
        setStatus('draft')

        setIsModalOpen(true)

    }

    const closeModal = () => {

        setIsModalOpen(false)

        setEditingQuote(null)

        setClientId('')
        setStatus('draft')

    }

    const loadQuotes = async () => {

        try {

            const response = await getQuotes()

            setQuotes(response.data)
            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de charger les devis"
            )

        }

    }

    const loadClients = async () => {

        try {

            const response = await getClients()

            setClients(response.data)

        } catch (err) {

            console.error(err)

        }

    }

    const filteredQuotes = quotes.filter(
        quote =>
            (quote.number || '')
                .toLowerCase()
                .includes(search.toLowerCase())
    )

    const handleEditQuote = (quote) => {

        setEditingQuote(quote)

        setClientId(
            quote.client?.id || ''
        )

        setStatus(
            quote.status
        )

        setIsModalOpen(true)

    }

    const handleSaveQuote = async () => {

        try {

            if (editingQuote) {

                await updateQuote(
                    editingQuote.id,
                    clientId,
                    status
                )

            } else {

                await createQuote(
                    clientId,
                    status
                )

            }

            await loadQuotes()

            closeModal()

        } catch (err) {

            console.error(err)

            setError(
                "Impossible d'enregistrer le devis"
            )

        }

    }

    const handleDeleteQuote = async (id) => {

        const confirmed = window.confirm(
            'Supprimer ce devis ?'
        )

        if (!confirmed) {
            return
        }

        try {

            await deleteQuote(id)

            await loadQuotes()

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de supprimer le devis"
            )

        }

    }

    useEffect(() => {

        const fetchData = async () => {

            await loadQuotes()
            await loadClients()

        }

        fetchData()

    }, [])

    return (
        <>

            <QuoteModal
                isOpen={isModalOpen}
                editingQuote={editingQuote}
                clients={clients}
                clientId={clientId}
                setClientId={setClientId}
                status={status}
                setStatus={setStatus}
                onSave={handleSaveQuote}
                onClose={closeModal}
            />

            <div className="space-y-6">

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

                <div className="flex items-center justify-between">

                    <div>

                        <h2 className="text-3xl font-bold">
                            Devis
                        </h2>

                        <p className="text-base-content/60">
                            Gérez vos devis
                        </p>

                    </div>

                    <button
                        className="btn btn-primary rounded-lg"
                        onClick={handleNewQuote}
                    >
                        + Nouveau devis
                    </button>

                </div>

                <input
                    type="text"
                    placeholder="🔍 Rechercher un devis..."
                    className="input input-bordered w-full bg-base-200 border-base-300 rounded-lg"
                    value={search}
                    onChange={(e) =>
                        setSearch(e.target.value)
                    }
                />

                <QuoteTable
                    quotes={filteredQuotes}
                    onEdit={handleEditQuote}
                    onDelete={handleDeleteQuote}
                />

            </div>

        </>
    )
}