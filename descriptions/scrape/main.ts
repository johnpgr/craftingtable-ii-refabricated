import fs from "node:fs"
import {JSDOM} from "jsdom"

const WIKI_URL = "https://minecraft.wiki/w/"
const args = process.argv.slice(2)
const INPUT_FILE = args[0]
const OUTPUT_FILE = args[1]

if(!INPUT_FILE || !OUTPUT_FILE) {
    console.error("❌ Usage: node scrape/main.ts <input_file> <output_file>")
    process.exit(1)
}

// Validate that input file exists
if (!fs.existsSync(INPUT_FILE)) {
    console.error(`❌ Input file not found: ${INPUT_FILE}`)
    process.exit(1)
}

console.log(`📁 Using input file: ${INPUT_FILE}`)
console.log(`📁 Using output file: ${OUTPUT_FILE}`)

const itemsData: Record<string, string> = JSON.parse(fs.readFileSync(INPUT_FILE, "utf-8"))

const results: Record<string, string> = {}

// Load existing results if output file exists
if (fs.existsSync(OUTPUT_FILE)) {
    const existingData = JSON.parse(fs.readFileSync(OUTPUT_FILE, "utf-8"))
    Object.assign(results, existingData)
}

const itemKeys = Object.keys(itemsData)

console.log(`🚀 Starting to scrape ${itemKeys.length} items with fetch...`);

for (let i = 0; i < itemKeys.length; i++) {
    const itemKey = itemKeys[i]!
    
    // Skip if already processed
    if (results[itemKey] !== undefined) {
        console.log(`ℹ️ Skipping already processed item: ${itemKey}`)
        continue
    }

    const parts = itemKey.split(".")

    if(parts.at(1) !== "minecraft") {
        console.log("ℹ️ Skipping non-minecraft item:", itemKey)
        results[itemKey] = ""
        continue
    }

    const name = parts.at(-1)
    const url = WIKI_URL + name

    try {
        console.log(`🔍 Progress: ${i + 1}/${itemKeys.length} - ${itemKey}`);
        const res = await fetch(url)
        if (res.status !== 200) throw new Error(`❌ Failed to fetch ${url}: ${res.statusText}`)

        const html = await res.text()

        const dom = new JSDOM(html)
        const doc = dom.window.document
        const description = doc.querySelector(".mw-content-ltr > p")
        if (!description) {
            console.warn(`⚠️  No description found for ${itemKey} at ${url}`);
            results[itemKey] = ""
            continue
        }
         // Remove wiki links
        results[itemKey] = description.textContent
            .trim() // Remove leading/trailing whitespace
            .replace(/\[[^\]]*\]/g, "") // Remove reference brackets like [1], [2], etc.
        console.log(`✅ Found description for ${itemKey}`);

    } catch (error) {
        console.error(`❌ Error scraping ${itemKey}:`, error);
        results[itemKey] = ""
    }

    // Delay between requests
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Save progress
    fs.writeFileSync(OUTPUT_FILE, JSON.stringify(results, null, 2));
}