import { execSync } from 'node:child_process'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const appDir = resolve(dirname(fileURLToPath(import.meta.url)), '..')
process.chdir(appDir)

try {
  const repoRoot = execSync('git rev-parse --show-toplevel', {
    encoding: 'utf8',
  }).trim()

  const hooksPath = 'bu_app/.husky'

  execSync(`git config --local core.hooksPath ${hooksPath}`, {
    cwd: repoRoot,
    stdio: 'inherit',
  })

  console.log(`[husky] Hooks configured at: ${hooksPath}`)
} catch {
  console.log('[husky] Git repository not detected. Skipping hooks setup.')
}
