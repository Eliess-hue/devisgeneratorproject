import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

import {
    getQuoteById,
    deleteQuoteLine,
    addQuoteLine,
    updateQuoteLine
} from '../api/apiQuote'
import {usePageTitle} from "../context/PageTitleContext.jsx"

import QuoteLinesTable from '../components/quotelines/QuoteLinesTable.jsx'
import QuoteTotalsCard from '../components/quotelines/QuoteTotalsCard.jsx'
import QuoteClientCard from '../components/quotelines/QuoteClientCard.jsx'
import QuoteInfoCard from '../components/quotelines/QuoteInfoCard.jsx'
import QuoteHeader from '../components/quotelines/QuoteHeader.jsx'
import QuoteLineForm from '../components/quotelines/QuoteLineForm.jsx'
import QuoteLineModal from '../components/quotelines/QuoteLineModal.jsx'
import ConfirmationModal from "../components/common/ConfirmationModal.jsx";

export default function QuoteDetailPage() {

    const navigate = useNavigate()

    const { id } = useParams()

    const { setPageTitle } = usePageTitle()

    const [quote, setQuote] = useState(null)

    const [loading, setLoading] = useState(true)

    const [error, setError] = useState(null)

    const [isLineModalOpen, setIsLineModalOpen] =
        useState(false)

    const [isDeleteModalOpen, setIsDeleteModalOpen] =
        useState(false)

    const [lineToDelete, setLineToDelete] =
        useState(null)

    const [editingLine, setEditingLine] =
        useState(null)

    const [lineForm, setLineForm] = useState({
        description: '',
        quantity: '',
        unitPrice: ''
    })

    const [lineError, setLineError] =
        useState(null)

    const loadQuote = async () => {

        try {

            const response =
                await getQuoteById(id)

            setQuote(
                response.data
            )

            setPageTitle(
                response.data.number
            )

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                'Impossible de charger le devis'
            )

        } finally {

            setLoading(false)

        }

    }

    const resetLineForm = () => {

        setLineForm({
            description: '',
            quantity: '',
            unitPrice: ''
        })

        setEditingLine(null)

        setLineError(null)

    }

    const handleAddLine = async () => {

        if (
            !lineForm.description.trim() ||
            !lineForm.quantity ||
            !lineForm.unitPrice
        ) {

            setLineError(
                'Tous les champs sont obligatoires'
            )

            return

        }

        if (
            Number(lineForm.quantity) <= 0 ||
            Number(lineForm.unitPrice) <= 0
        ) {

            setLineError(
                'Les valeurs doivent être supérieures à 0'
            )

            return

        }

        try {

            const payload = {
                description: lineForm.description,
                quantity: Number(lineForm.quantity),
                unitPrice: Number(lineForm.unitPrice)
            }

            if (editingLine) {

                await updateQuoteLine(
                    id,
                    editingLine.id,
                    payload
                )

            } else {

                await addQuoteLine(
                    id,
                    payload
                )

            }

            resetLineForm()

            setIsLineModalOpen(false)

            await loadQuote()

        } catch (err) {

            console.error(err)

            setLineError(
                editingLine
                    ? "Impossible de modifier la ligne"
                    : "Impossible d'ajouter la ligne"
            )

        }

    }

    const handleEditLine = (line) => {

        setEditingLine(line)

        setLineForm({
            description: line.description,
            quantity: String(line.quantity),
            unitPrice: String(line.unitPrice)
        })

        setLineError(null)

        setIsLineModalOpen(true)

    }

    const handleDeleteLine = (
        lineId
    ) => {

        setLineToDelete(lineId)

        setIsDeleteModalOpen(true)

    }

    const handleConfirmDeleteLine = async () => {

        try {

            await deleteQuoteLine(
                id,
                lineToDelete
            )

            await loadQuote()

            closeDeleteModal()

        } catch (err) {

            console.error(err)

            setError(
                'Impossible de supprimer la ligne'
            )

        }

    }

    const closeDeleteModal = () => {

        setIsDeleteModalOpen(false)

        setLineToDelete(null)

    }

    useEffect(() => {

        loadQuote()

    }, [id])

    useEffect(() => {

        return () => {
            setPageTitle('DevisApp')
        }

    }, [])

    if (loading) {
        return <p>Chargement...</p>
    }

    if (error) {
        return <p>{error}</p>
    }

    return (

        <div className="space-y-8">

            <div>

                <button
                    className="
                        px-5
                        py-2
                        rounded-lg
                        border
                        border-[#2E2E2E]
                        bg-transparent
                        text-white
                        hover:bg-[#111111]
                        transition
                        text-sm
                      text-base-content/70
                    "
                    onClick={() => navigate('/quotes')}
                >
                    ← Retour aux devis
                </button>

            </div>

            <QuoteHeader
                quote={quote}
                onAddLine={() =>
                    setIsLineModalOpen(true)
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

                <QuoteClientCard
                    client={quote.client}
                />

                <QuoteInfoCard
                    quote={quote}
                />

            </div>

            <QuoteLineModal
                isOpen={isLineModalOpen}
                onClose={() => {

                    resetLineForm()

                    setIsLineModalOpen(false)

                }}
            >

                <QuoteLineForm
                    lineForm={lineForm}
                    setLineForm={setLineForm}
                    onAddLine={handleAddLine}
                    lineError={lineError}
                    editingLine={editingLine}
                />

            </QuoteLineModal>

            <ConfirmationModal
                isOpen={isDeleteModalOpen}
                title="Supprimer la ligne"
                message="Voulez-vous vraiment supprimer cette ligne ?"
                onConfirm={handleConfirmDeleteLine}
                onClose={closeDeleteModal}
                confirmLabel="Supprimer"
            />

            <QuoteLinesTable
                lines={quote.lines}
                onDeleteLine={handleDeleteLine}
                onEditLine={handleEditLine}
            />

            <QuoteTotalsCard
                totalHt={quote.totalHt}
                totalTva={quote.totalTva}
                totalTtc={quote.totalTtc}
            />

        </div>

    )

}