export default function QuoteLineModal({
                                           isOpen,
                                           onClose,
                                           title,
                                           children
                                       }) {

    if (!isOpen) {
        return null
    }

    return (

        <div className="
            fixed
            inset-0
            bg-black/60
            flex
            items-center
            justify-center
            z-50
        ">

            <div className="
                bg-base-200
                border
                border-base-300
                rounded-xl
                p-6
                w-full
                max-w-xl
            ">

                <div className="flex justify-between mb-4">

                    <h2 className="text-xl font-semibold">
                        {title}
                    </h2>

                    <button
                        onClick={onClose}
                        className="
                            btn
                            btn-xs
                            btn-ghost
                            rounded-lg
                            bg-[var(--color-error-bg)]
                            text-[var(--color-error-text)]
                            hover:bg-error/10
                        "
                    >
                        ✕
                    </button>

                </div>

                {children}

            </div>

        </div>

    )

}