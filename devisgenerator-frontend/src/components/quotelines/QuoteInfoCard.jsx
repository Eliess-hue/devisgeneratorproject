export default function QuoteInfoCard({ quote, onStatusChange }) {

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
                            ).toLocaleDateString("fr-FR")}
                        </span>

                    </div>

                    <div className="flex justify-between items-center">

                        <span className="text-base-content/60">
                            Statut
                        </span>

                        <select
                            className="select select-bordered select-sm w-40 rounded-lg"
                            value={quote.status}
                            onChange={(e) =>
                                onStatusChange(e.target.value)
                            }
                        >
                            <option value="DRAFT">
                                Brouillon
                            </option>

                            <option value="PENDING">
                                En attente
                            </option>

                            <option value="ACCEPTED">
                                Accepté
                            </option>

                            <option value="REFUSED">
                                Refusé
                            </option>

                            <option value="EXPIRED">
                                Expiré
                            </option>

                        </select>

                    </div>

                </div>

            </div>

        </div>

    )

}