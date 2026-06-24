import { createContext, useContext, useState } from 'react'

const PageTitleContext = createContext()

export function PageTitleProvider({ children }) {

    const [pageTitle, setPageTitle] =
        useState(null)

    return (
        <PageTitleContext.Provider
            value={{
                pageTitle,
                setPageTitle
            }}
        >
            {children}
        </PageTitleContext.Provider>
    )
}

export function usePageTitle() {

    return useContext(PageTitleContext)

}