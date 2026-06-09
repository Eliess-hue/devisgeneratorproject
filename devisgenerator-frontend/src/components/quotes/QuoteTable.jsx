import QuoteStatusBadge
    from '../quotelines/QuoteStatusBadge.jsx'
import {useNavigate} from "react-router-dom";

export default function QuoteTable({
                                       quotes,
                                       onEdit,
                                       onDelete
                                   }) {

    const navigate = useNavigate();

    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold">

                    Mes devis

                </h3>

                {quotes.length === 0 ? (

                    <div className="py-10 text-center">

                        <p className="text-lg font-medium">

                            Aucun devis

                        </p>

                        <p className="text-base-content/60">

                            Créez votre premier devis.

                        </p>

                    </div>

                ) : (

                    <div className="overflow-x-auto">

                        <table className="table">

                            <thead>

                            <tr>

                                <th>N°</th>

                                <th>Client</th>

                                <th>Statut</th>

                                <th>TTC</th>

                                <th>Date</th>

                                <th>Actions</th>

                            </tr>

                            </thead>

                            <tbody>

                            {quotes.map(quote => (

                                <tr
                                    key={quote.id}
                                >

                                    <td>
                                        {quote.number}
                                    </td>

                                    <td>
                                        {
                                            quote.client?.name
                                        }
                                    </td>

                                    <td>
                                        <QuoteStatusBadge
                                            status={
                                                quote.status
                                            }
                                        />
                                    </td>

                                    <td>
                                        {Number(
                                            quote.totalTtc ?? 0
                                        ).toFixed(2)} €
                                    </td>

                                    <td>
                                        {new Date(
                                            quote.createdAt
                                        ).toLocaleDateString('fr-FR')}
                                    </td>

                                    <td>

                                        <button
                                            className="btn btn-xs bg-blue-900 text-blue-400 border-none hover:bg-blue-800 rounded-lg"
                                            onClick={() =>
                                                onEdit(
                                                    quote
                                                )
                                            }
                                        >
                                            Modifier
                                        </button>

                                        |

                                        <button
                                            className="btn btn-xs bg-red-950 text-red-300 border-none hover:bg-red-900 rounded-lg"
                                            onClick={() =>
                                                onDelete(
                                                    quote.id
                                                )
                                            }
                                        >
                                            Supprimer
                                        </button>

                                        |

                                        <button
                                            className="btn btn-xs bg-base-300 text-base-content border-none hover:bg-base-100 rounded-lg"
                                            onClick={() => navigate(`/quotes/${quote.id}`)}
                                        >
                                            Voir
                                        </button>

                                    </td>

                                </tr>

                            ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </div>

        </div>

    )
}