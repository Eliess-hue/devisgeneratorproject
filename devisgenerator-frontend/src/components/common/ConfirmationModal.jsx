export default function ConfirmationModal({
                                              isOpen,
                                              title,
                                              message,
                                              onConfirm,
                                              onClose,
                                              confirmLabel = 'Confirmer',
                                              cancelLabel = 'Annuler'
                                          }) {

    if (!isOpen) {
        return null
    }

    return (
        <dialog className="modal modal-open">

            <div className="modal-box">

                <h3 className="font-bold text-lg mb-4">
                    {title}
                </h3>

                <p className="mb-6">
                    {message}
                </p>

                <div className="modal-action">

                    <button
                        className="btn rounded-lg"
                        onClick={onClose}
                    >
                        {cancelLabel}
                    </button>

                    <button
                        className="btn bg-red-950 text-red-300 border-none hover:bg-red-900 rounded-lg"
                        onClick={onConfirm}
                    >
                        {confirmLabel}
                    </button>

                </div>

            </div>

        </dialog>
    )
}