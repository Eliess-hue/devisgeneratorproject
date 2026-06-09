export default function QuoteLinesTable({
                                            lines,
                                            onDeleteLine
                                        }) {

    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold text-lg">

                    Lignes du devis

                </h3>

                {lines.length === 0 ? (

                    <div className="py-8 text-center">

                        <p className="text-base-content/60">

                            Aucune ligne dans ce devis.

                        </p>

                    </div>

                ) : (

                    <div className="overflow-x-auto">

                        <table className="table">

                            <thead>

                            <tr>

                                <th>Description</th>

                                <th>Qté</th>

                                <th>Prix unitaire</th>

                                <th>Total</th>

                                <th>Actions</th>

                            </tr>

                            </thead>

                            <tbody>

                            {lines.map(line => (

                                <tr key={line.id}>

                                    <td>
                                        {line.description}
                                    </td>

                                    <td>
                                        {line.quantity}
                                    </td>

                                    <td>
                                        {Number(
                                            line.unitPrice
                                        ).toFixed(2)} €
                                    </td>

                                    <td>
                                        {Number(
                                            line.total
                                        ).toFixed(2)} €
                                    </td>

                                    <td>

                                        <button
                                            className="
                                                btn
                                                btn-xs
                                                bg-red-950
                                                text-red-300
                                                border-none
                                                hover:bg-red-900
                                                rounded-lg
                                            "
                                            onClick={() =>
                                                onDeleteLine(
                                                    line.id
                                                )
                                            }
                                        >
                                            Supprimer
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