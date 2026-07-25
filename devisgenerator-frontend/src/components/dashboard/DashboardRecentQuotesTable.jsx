import QuoteStatusBadge from "../quotelines/QuoteStatusBadge.jsx"
import usePdf from "../../hooks/usePdf"

import { useNavigate } from 'react-router-dom'

export default function DashboardRecentQuotesTable({quotes}) {

    const navigate = useNavigate()

    const {
        openPdf
    } = usePdf()

    return (

        <div className="card bg-base-200 shadow-sm rounded-lg">

            <div className="card-body">

                <h2 className="card-title">
                    Derniers devis
                </h2>

                <div className="overflow-x-auto">

                    <table className="table">

                        <thead>
                        <tr>
                            <th>N° Devis</th>
                            <th>Client</th>
                            <th>Statut</th>
                            <th>Date</th>
                            <th>Total TTC</th>
                            <th>Actions</th>
                        </tr>
                        </thead>

                        <tbody>

                        {quotes.map(quote => (

                            <tr key={quote.quoteNumber}>

                                <td>
                                    {quote.quoteNumber}
                                </td>

                                <td>
                                    {quote.clientName}
                                </td>

                                <td>
                                    <QuoteStatusBadge
                                        status={quote.status}
                                    />
                                </td>

                                <td>
                                    {new Date(
                                        quote.createdAt
                                    ).toLocaleDateString('fr-FR')}
                                </td>

                                <td>
                                    {quote.totalTtc} €
                                </td>

                                <td>

                                    <div className="flex items-center gap-2 whitespace-nowrap">

                                        <button
                                            className="btn btn-xs bg-base-300 text-base-content border-none hover:bg-base-100 rounded-lg"
                                            onClick={() =>
                                                navigate(
                                                    `/quotes/${quote.id}`
                                                )
                                            }
                                        >
                                            Voir
                                        </button>

                                        <button
                                            className="btn btn-xs bg-blue-900 text-blue-400 border-none hover:bg-blue-800 rounded-lg"
                                            disabled
                                        >
                                            Modifier
                                        </button>

                                        <button
                                            className="btn btn-xs btn-primary rounded-lg"
                                            onClick={() => openPdf(quote.id)}
                                        >
                                            PDF
                                        </button>

                                    </div>

                                </td>

                            </tr>

                        ))}

                        </tbody>

                    </table>

                </div>

            </div>

        </div>

    )

}