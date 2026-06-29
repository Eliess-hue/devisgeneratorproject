export default function QuoteLineForm({
                                          lineForm,
                                          setLineForm,
                                          onAddLine,
                                          lineError,
                                          editingLine
                                      }) {

    const quantity =
        Number(lineForm.quantity) || 0;

    const unitPrice =
        Number(lineForm.unitPrice) || 0;

    const vatRate =
        Number(lineForm.vatRate);

    const totalHt =
        quantity * unitPrice;

    const totalTva =
        totalHt * vatRate;

    const totalTtc =
        totalHt + totalTva;



    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold text-lg">
                    {editingLine
                        ? 'Modifier la ligne'
                        : 'Ajouter une ligne'}
                </h3>

                <div className="grid grid-cols-1 md:grid-cols-5 gap-3">

                    <input
                        type="text"
                        placeholder="Description"
                        className="input input-bordered rounded-lg"
                        value={lineForm.description}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                description: e.target.value
                            })
                        }
                    />

                    <input
                        type="number"
                        placeholder="Quantité"
                        className="input input-bordered rounded-lg"
                        value={lineForm.quantity}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                quantity: e.target.value
                            })
                        }
                    />

                    <input
                        type="number"
                        step="0.01"
                        placeholder="Prix unitaire"
                        className="input input-bordered rounded-lg"
                        value={lineForm.unitPrice}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                unitPrice: e.target.value
                            })
                        }
                    />

                    <select
                        className="select select-bordered rounded-lg"
                        value={lineForm.vatRate}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                vatRate: Number(e.target.value)
                            })
                        }
                    >
                        <option value={0}>0 %</option>
                        <option value={0.055}>5.5 %</option>
                        <option value={0.10}>10 %</option>
                        <option value={0.20}>20 %</option>
                    </select>

                    <button
                        className="btn btn-primary rounded-lg"
                        onClick={onAddLine}
                    >
                        {editingLine
                            ? 'Modifier'
                            : 'Ajouter'}
                    </button>

                </div>

                {totalHt > 0 && (

                    <div className="mt-4 p-4 rounded-lg bg-base-100 border border-base-300">

                        <h4 className="font-semibold mb-2">
                            Aperçu
                        </h4>

                        <div className="space-y-1 text-sm">

                            <p>
                                HT : {totalHt.toFixed(2)} €
                            </p>

                            <p>
                                TVA ({(vatRate * 100).toFixed(1).replace(".0", "")} %) :
                                {" "}
                                {totalTva.toFixed(2)} €
                            </p>

                            <p className="font-semibold">
                                TTC : {totalTtc.toFixed(2)} €
                            </p>

                        </div>

                    </div>

                )}

                {lineError && (

                    <div className="mb-4 mt-3 rounded-lg border px-4 py-3"
                         style={{
                             backgroundColor: '#450A0A',
                             borderColor: '#7F1D1D',
                             color: '#FCA5A5'
                         }}
                    >

                        <span>
                            {lineError}
                        </span>

                    </div>

                )}

            </div>

        </div>

    )

}