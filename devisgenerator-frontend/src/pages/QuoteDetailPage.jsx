import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

import useQuoteDetail from "../hooks/useQuoteDetail.js";
import {usePageTitle} from "../context/PageTitleContext.jsx"

import QuoteLinesTable from '../components/quotelines/QuoteLinesTable.jsx'
import QuoteTotalsCard from '../components/quotelines/QuoteTotalsCard.jsx'
import QuoteClientCard from '../components/quotelines/QuoteClientCard.jsx'
import QuoteInfoCard from '../components/quotelines/QuoteInfoCard.jsx'
import QuoteHeader from '../components/quotelines/QuoteHeader.jsx'
import QuoteLineForm from '../components/quotelines/QuoteLineForm.jsx'
import QuoteLineModal from '../components/quotelines/QuoteLineModal.jsx'
import ConfirmationModal from "../components/common/ConfirmationModal.jsx"
import QuoteDetailPageSkeleton from "../components/skeletons/QuoteDetailPageSkeleton.jsx"
import usePdf from "../hooks/usePdf.js"
import useEmail from "../hooks/useEmail.js"
import Alert from "../components/common/Alert.jsx";

export default function QuoteDetailPage() {

    const navigate = useNavigate()
    const { id } = useParams()
    const { setPageTitle } = usePageTitle()

    const {
        quote,
        loading,
        error,
        saveLine,
        removeLine,
        updateStatus,
        duplicate
    } = useQuoteDetail(id)

    const {
        openPdf,
        error: pdfError,
        clearError: clearPdfError
    } = usePdf()

    const {
        sendEmail,
        isSending,
        error: emailError,
        clearError: clearEmailError
    } = useEmail()

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
        unitPrice: '',
        vatRate: '0.20'
    })

    const [lineError, setLineError] =
        useState(null)

    const [successMessage, setSuccessMessage] =
        useState(null)

    const actionError = pdfError || emailError

    const clearActionError = () => {

        clearPdfError()
        clearEmailError()

    }

    useEffect(() => {

        if (quote) {
            setPageTitle(quote.number)
        }

    }, [quote])

    useEffect(() => {

        return () => setPageTitle(null)

    }, [])

    const resetLineForm = () => {

        setLineForm({
            description: '',
            quantity: '',
            unitPrice: '',
            vatRate: '0.20'
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

            setLineError("Tous les champs sont obligatoires");
            return

        }

        if (
            Number(lineForm.quantity) <= 0 ||
            Number(lineForm.unitPrice) <= 0
        ) {

            setLineError("Les valeurs doivent être supérieures à 0")
            return

        }

        try {

            await saveLine({

                lineId: editingLine?.id,

                description: lineForm.description,
                quantity: Number(lineForm.quantity),
                unitPrice: Number(lineForm.unitPrice),
                vatRate: Number(lineForm.vatRate)

            })

            resetLineForm();
            setIsLineModalOpen(false);

        } catch (err) {

            console.error(err)

        }

    }

    const handleEditLine = (line) => {

        setEditingLine(line)

        setLineForm({
            description: line.description,
            quantity: String(line.quantity),
            unitPrice: String(line.unitPrice),
            vatRate: String(line.vatRate)
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

            await removeLine(lineToDelete);

            closeDeleteModal();

        } catch (err) {

            console.error(err);

        }

    }

    const handleDuplicate = async () => {

        try {

            const newId = await duplicate();

            navigate(`/quotes/${newId}`);

        } catch (err) {

            console.error(err);

        }

    }

    const handleSendEmail = async () => {

        try {

            await sendEmail(quote.id)

            setSuccessMessage(
                `Le devis a été envoyé à ${quote.client.email}`
            )

        } catch (err) {

            console.error(err)

        }

    }

    const handleStatusChange = async (newStatus) => {

        try {

            await updateStatus(newStatus);

        } catch (err) {

            console.error(err);

        }

    };

    const closeDeleteModal = () => {

        setIsDeleteModalOpen(false)
        setLineToDelete(null)

    }

    if (loading) {
        return <QuoteDetailPageSkeleton/>
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
                        border-[var(--color-neutral)]
                        bg-transparent
                        text-base-content/70
                        hover:bg-[var(--color-base-200)]
                        transition
                        text-sm
                    "
                    onClick={() => navigate('/quotes')}
                >
                    ← Retour aux devis
                </button>

            </div>

            {successMessage && (
                <Alert
                    type="success"
                    autoClose={5000}
                    onClose={() => setSuccessMessage(null)}
                >
                    {successMessage}
                </Alert>
            )}

            {actionError && (
                <Alert
                    type="error"
                    autoClose={5000}
                    onClose={clearActionError}
                >
                    {actionError}
                </Alert>
            )}

            <QuoteHeader
                quote={quote}
                onAddLine={() =>
                    setIsLineModalOpen(true)
                }
                onDuplicate={handleDuplicate}
                onPdf={() => openPdf(id)}
                onSendEmail={handleSendEmail}
                isSending={isSending}
            />

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

                <QuoteClientCard
                    client={quote.client}
                />

                <QuoteInfoCard
                    quote={quote}
                    onStatusChange={handleStatusChange}
                />

            </div>

            <QuoteLineModal
                isOpen={isLineModalOpen}
                onClose={() => {
                    resetLineForm()
                    setIsLineModalOpen(false)
                }}
                title={
                    editingLine
                        ? "Modifier la ligne"
                        : "Ajouter une ligne"
                }
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