import app from "./app"
import dotenv from "dotenv"

dotenv.config()

const PORT = process.env.PORT || 3004

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`)
})
