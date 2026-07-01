import {useState, useEffect} from "react"
import useQuotes from "../hooks/useQuotes"

import {getClients} from "../api/apiClient.js"
import QuoteModal from "../components/quotes/QuoteModal.jsx"
import QuoteTable from "../components/quotes/QuoteTable.jsx"
import ConfirmationModal from "../components/common/ConfirmationModal.jsx"
import QuotesPageSkeleton from "../components/skeletons/QuotesPageSkeleton.jsx"
import Pagination from "../components/common/Pagination.jsx"
import Alert from "../components/common/Alert.jsx"

export default function QuotesPage() {

    const [uiError, setUiError] = useState(null);
    const [clients, setClients] = useState([])
    const [isModalOpen, setIsModalOpen] = useState(false)
    const [editingQuote, setEditingQuote] = useState(null)
    const [clientId, setClientId] = useState('')
    const [status, setStatus] = useState('DRAFT')
    const [search, setSearch] = useState('')
    const [statusFilter, setStatusFilter] = useState('')
    const [from, setFrom] = useState('')
    const [to, setTo] = useState('')
    const [page, setPage] = useState(0)

    const [isDeleteModalOpen, setIsDeleteModalOpen] =
        useState(false)

    const [quoteToDelete, setQuoteToDelete] =
        useState(null)

    const {
        quotes,
        loading,
        error,
        totalPages,
        totalElements,
        loadQuotes,
        saveQuote,
        removeQuote,
        duplicateQuote,
    } = useQuotes()

    const refresh = () =>
        loadQuotes({

            search,
            status: statusFilter,
            from,
            to,
            page,
            size: 10

        })

    const handleNewQuote = () => {
        if (clients.length === 0) {

            setUiError(
                "Vous devez créer un client avant de créer un devis."
            );

            return

        }

        setUiError(null)
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
        setUiError(null);

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

            await saveQuote({

                id: editingQuote?.id,
                clientId,
                status

            });

            await refresh();

            closeModal();

        } catch (err) {

            console.error(err);

        }

    }

    const handleDeleteQuote = (id) => {

        setQuoteToDelete(id)

        setIsDeleteModalOpen(true)

    }

    const handleConfirmDelete = async () => {

        try {

            await removeQuote(
                quoteToDelete
            );

            await refresh();

            closeDeleteModal();

        } catch (err) {

            console.error(err);

        }

    }

    const closeDeleteModal = () => {

        setIsDeleteModalOpen(false)

        setQuoteToDelete(null)

    }

    const handleDuplicateQuote = async (id) => {

        try {

            await duplicateQuote(id);

            await refresh();

        } catch (err) {

            console.error(err);

        }

    }

    useEffect(() => {
        setPage(0)
    }, [search, statusFilter, from, to])

    useEffect(() => {

        const timer = setTimeout(async () => {
            refresh();
        }, 300);

        return () => clearTimeout(timer);

    }, [
        search,
        statusFilter,
        from,
        to,
        page
    ]);

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

                {(uiError || error) && (
                    <Alert type="error" className="mb-4">
                        {uiError || error}
                    </Alert>
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

                <Pagination
                    page={page}
                    totalPages={totalPages}
                    totalElements={totalElements}
                    onPageChange={setPage}
                />

            </div>

        </>
    )
}