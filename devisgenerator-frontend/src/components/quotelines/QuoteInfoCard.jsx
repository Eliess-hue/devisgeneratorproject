import QuoteStatusBadge from './QuoteStatusBadge.jsx'

export default function QuoteInfoCard({
                                          quote
                                      }) {

    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold">

                    Informations devis

                </h3>

                <div className="space-y-2">

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            N° devis
                        </span>

                        <span>
                            {quote.number}
                        </span>

                    </div>

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            Date
                        </span>

                        <span>
                            {new Date(
                                quote.createdAt
                            ).toLocaleDateString(
                                'fr-FR'
                            )}
                        </span>

                    </div>

                    <div className="flex justify-between items-center">

                        <span className="text-base-content/60">
                            Statut
                        </span>

                        <QuoteStatusBadge
                            status={quote.status}
                        />

                    </div>

                </div>

            </div>

        </div>

    )

}