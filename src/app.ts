import express from "express"
import cors from "cors"
import morgan from "morgan"

const app = express()

app.use(
    cors({
        origin: process.env.GITHUB_IO_PAGE,
        methods: "*",
    }),
)
app.use(morgan("dev"))
app.use(express.json())

app.get("/", (req, res) => {
    res.send("API 서버가 정상적으로 동작하고 있습니다.")
})

app.use((err: Error, _req: express.Request, res: express.Response) => {
    console.error(err.stack)
    res.status(500).send("서버에서 오류가 발생했습니다.")
})

export default app
