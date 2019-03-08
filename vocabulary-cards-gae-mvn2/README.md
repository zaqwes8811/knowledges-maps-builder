appengine-skeleton
=============================

This is a generated application from the appengine-skeleton archetype.


# Purge data
https://stackoverflow.com/questions/10067848/remove-folder-and-its-contents-from-git-githubs-history

git filter-branch --tree-filter 'rm -rf projects/research/test_data' --prune-empty HEAD
git for-each-ref --format="%(refname)" refs/original/ | xargs -n 1 git update-ref -d
echo projects/research/test_data/ >> .gitignore
git add .gitignore
git commit -m 'Removing projects/research/test_data from git history'
git gc
git push origin master --force

impls/vcard-gae-maven/target

git filter-branch --tree-filter 'rm -rf impls/vcard-gae-maven/target' --prune-empty HEAD
git for-each-ref --format="%(refname)" refs/original/ | xargs -n 1 git update-ref -d
echo impls/vcard-gae-maven/target/ >> .gitignore
git add .gitignore
git commit -m 'Removing impls/vcard-gae-maven/target from git history'
git gc
git push origin master --force

#
git filter-branch --tree-filter 'rm -rf war' --prune-empty HEAD
git for-each-ref --format="%(refname)" refs/original/ | xargs -n 1 git update-ref -d


# Install mvn2 ubuntu

sudo apt-get install maven
 
# Build
https://www.vogella.com/tutorials/ApacheMaven/article.html

# big files
https://stackoverflow.com/questions/10622179/how-to-find-identify-large-commits-in-git-history

git rev-list --objects --all \
| git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' \
| sed -n 's/^blob //p' \
| sort --numeric-sort --key=2 \
| cut -c 1-12,41- \
| $(command -v gnumfmt || echo numfmt) --field=2 --to=iec-i --suffix=B --padding=7 --round=nearest

 git log --all --full-history --  war/WEB-INF/appengine-generated/local_db.bin

 https://dalibornasevic.com/posts/2-permanently-remove-files-and-folders-from-a-git-repository