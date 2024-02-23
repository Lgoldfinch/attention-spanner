//> using dep com.lihaoyi::requests::0.8.0

val repoOwner = args.toSeq(0)
val repoName = args.toSeq(1)
val filePath = args.toSeq(2)
val commitSha = args.toSeq(3)
val githubToken = args.toSeq(4)

val apiUrl = s"https://api.github.com/repos/$repoOwner/$repoName/commits/$commitSha"

val r = requests.get(apiUrl, headers = Map("Authorization" -> s"token: $githubToken"))

println(r.text())
// scala-cli run scripts/ignoreBuildIf.sc -- Lgoldfinch attention-spanner .scalafmt.conf
//926f255...HEAD
// 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"