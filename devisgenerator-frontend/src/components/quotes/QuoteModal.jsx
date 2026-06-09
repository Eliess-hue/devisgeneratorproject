import { useState } from 'react'

export default function QuoteModal({
                                       isOpen,
                                       editingQuote,
                                       clients,
                                       clientId,
                                       setClientId,
                                       status,
                                       setStatus,
                                       onSave,
                                       onClose
                                   }) {

    const [clientError, setClientError] = useState('')

    if (!isOpen) {
        return null
    }

    const handleSave = () => {

        if (!clientId) {

            setClientError(
                'Veuillez sélectionner un client'
            )

            return

        }

        setClientError('')

        onSave()

    }

    const handleClose = () => {

        setClientError('')

        onClose()

    }

    return (
        <dialog className="modal modal-open">

            <div className="modal-box">

                <h3 className="font-bold text-lg mb-4">

                    {editingQuote
                        ? 'Modifier le devis'
                        : 'Nouveau devis'
                    }

                </h3>

                <div className="flex flex-col gap-4">

                    <select
                        className="select select-bordered rounded-lg"
                        value={clientId}
                        onChange={(e) => {

                            const value = e.target.value

                            setClientId(
                                value ? Number(value) : ''
                            )

                            if (clientError) {
                                setClientError('')
                            }

                        }}
                    >

                        <option value="">
                            Sélectionner un client
                        </option>

                        {clients.map(client => (

                            <option
                                key={client.id}
                                value={client.id}
                            >
                                {client.name}
                            </option>

                        ))}

                    </select>

                    {clientError && (

                        <p
                            className="text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {clientError}
                        </p>

                    )}

                    {clients.length === 0 && (

                        <p className="text-warning text-sm">

                            Aucun client disponible.
                            Créez d'abord un client.

                        </p>

                    )}

                    <select
                        className="select select-bordered rounded-lg"
                        value={status}
                        onChange={(e) =>
                            setStatus(
                                e.target.value
                            )
                        }
                    >

                        <option value="draft">
                            Brouillon
                        </option>

                        <option value="pending">
                            En attente
                        </option>

                        <option value="accepted">
                            Accepté
                        </option>

                        <option value="refused">
                            Refusé
                        </option>

                    </select>

                </div>

                <div className="modal-action">

                    <button
                        className="btn rounded-lg"
                        onClick={handleClose}
                    >
                        Annuler
                    </button>

                    <button
                        className="btn btn-primary rounded-lg"
                        onClick={handleSave}
                    >
                        Enregistrer
                    </button>

                </div>

            </div>

        </dialog>
    )
}