import {useState, useEffect} from "react"
import {
    searchQuotes,
    createQuote,
    updateQuote,
    deleteQuote,
    duplicateQuote
} from "../api/apiQuote.js"

import {getClients} from "../api/apiClient.js"
import QuoteModal from "../components/quotes/QuoteModal.jsx"
import QuoteTable from "../components/quotes/QuoteTable.jsx"
import ConfirmationModal from "../components/common/ConfirmationModal.jsx"
import QuotesPageSkeleton from "../components/skeletons/QuotesPageSkeleton.jsx"

export default function QuotesPage() {

    const [quotes, setQuotes] = useState([])

    const [clients, setClients] = useState([])

    const [isModalOpen, setIsModalOpen] = useState(false)

    const [editingQuote, setEditingQuote] = useState(null)

    const [clientId, setClientId] = useState('')
    const [status, setStatus] = useState('DRAFT')

    const [search, setSearch] = useState('')
    const [isSearching, setIsSearching] = useState(false)
    const [statusFilter, setStatusFilter] = useState('')
    const [from, setFrom] = useState('')
    const [to, setTo] = useState('')

    const [error, setError] = useState(null)

    const [isDeleteModalOpen, setIsDeleteModalOpen] =
        useState(false)

    const [quoteToDelete, setQuoteToDelete] =
        useState(null)

    const [loading, setLoading] = useState(true)

    const handleNewQuote = () => {
        if (clients.length === 0) {

            setError(
                "Vous devez créer un client avant de créer un devis."
            )

            return

        }

        setEditingQuote(null)

        setClientId('')
        setStatus('DRAFT')

        setIsModalOpen(true)

    }

    const closeModal = () => {

        setIsModalOpen(false)

        setEditingQuote(null)

        setClientId('')
        setStatus('DRAFT')

    }

    const loadQuotes = async () => {

        if (!loading) {
            setIsSearching(true)
        }

        try {

            const response = await searchQuotes({
                search,
                status: statusFilter,
                from,
                to
            })

            setQuotes(response.data)
            setError(null)

        } catch (err) {

            console.error(err)

            setError("Impossible de charger les devis")

        } finally {

            setLoading(false)
            setIsSearching(false)

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

    const handleDeleteQuote = (id) => {

        setQuoteToDelete(id)

        setIsDeleteModalOpen(true)

    }

    const handleConfirmDelete = async () => {

        try {

            await deleteQuote(
                quoteToDelete
            )

            await loadQuotes()

            setIsDeleteModalOpen(false)

            setQuoteToDelete(null)

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de supprimer le devis"
            )

        }

    }

    const closeDeleteModal = () => {

        setIsDeleteModalOpen(false)

        setQuoteToDelete(null)

    }

    const handleDuplicateQuote = async (id) => {

        try {

            await duplicateQuote(id)

            await loadQuotes()

        } catch (err) {

            console.error(err)

            setError(
                "Impossible de dupliquer le devis"
            )

        }

    }

    useEffect(() => {
        const timer = setTimeout(() => {
            loadQuotes()
        }, 300)

        return () => clearTimeout(timer)
    }, [search, statusFilter, from, to])

    useEffect(() => {

        loadClients()

    }, [])

    if (loading) {
        return <QuotesPageSkeleton />
    }

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

            <ConfirmationModal
                isOpen={isDeleteModalOpen}
                title="Supprimer le devis"
                message="Voulez-vous vraiment supprimer ce devis ?"
                onConfirm={handleConfirmDelete}
                onClose={closeDeleteModal}
                confirmLabel="Supprimer"
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

                <div className="flex flex-wrap gap-4">

                    <input
                        type="text"
                        placeholder="🔍 Rechercher un devis..."
                        className="input input-bordered rounded-lg"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />

                    <select
                        className="select select-bordered rounded-lg"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        <option value="">Tous les statuts</option>
                        <option value="DRAFT">Brouillons</option>
                        <option value="PENDING">En attentes</option>
                        <option value="ACCEPTED">Acceptés</option>
                        <option value="REFUSED">Refusés</option>
                        <option value="EXPIRED">Expirés</option>
                    </select>

                    <input
                        type="date"
                        className="input input-bordered rounded-lg"
                        value={from}
                        onChange={(e) => setFrom(e.target.value)}
                    />

                    <input
                        type="date"
                        className="input input-bordered rounded-lg"
                        value={to}
                        onChange={(e) => setTo(e.target.value)}
                    />

                </div>

                <QuoteTable
                    quotes={quotes}
                    onEdit={handleEditQuote}
                    onDelete={handleDeleteQuote}
                    onDuplicate={handleDuplicateQuote}
                />

            </div>

        </>
    )
}