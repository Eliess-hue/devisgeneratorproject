import QuoteStatusBadge from './QuoteStatusBadge.jsx'

export default function QuoteHeader({
                                        quote,
                                        onAddLine,
                                        onDuplicate,
                                        onPdf
                                    }) {

    return (

        <div className="flex items-start justify-between">

            <div className="space-y-3">

                <h1 className="text-4xl font-bold">

                    {quote.number}

                </h1>

                <QuoteStatusBadge
                    status={quote.status}
                />

            </div>

            <div className="flex gap-3">

                <button
                    className="
                        btn
                        btn-primary
                        rounded-lg
                    "
                    onClick={onAddLine}
                >
                    + Nouvelle ligne
                </button>

                <button
                    className="
                        btn
                        btn-primary
                        rounded-lg
                    "

                    onClick={onPdf}
                >
                    PDF
                </button>

                <button
                    className="btn
                    bg-base-300 text-base-content
                    border-none hover:bg-base-100 rounded-lg"
                    onClick={onDuplicate}
                >
                    Dupliquer
                </button>

            </div>

        </div>

    )

}