export default function DashboardLayout({ children }: { children: React.ReactNode }){
    return (
        <div>
            <header>Dashboard header</header>
            <main>{children}</main>
        </div>
    )
}

