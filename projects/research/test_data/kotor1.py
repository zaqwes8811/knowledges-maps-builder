# encoding: utf-8

import os

fname = 'kotor1_raw.txt'
splitter = '@START@'

# http://stackoverflow.com/questions/15338092/the-request-to-api-call-datastore-v3-put-was-too-large
# https://code.google.com/p/googleappengine/issues/detail?id=78
filesize = os.path.getsize(fname)  # bytes
gae_max_entity_size = 1e6  # 
div = filesize / gae_max_entity_size + 1
assert div > 1

with open(fname) as f:
    content = f.readlines()

# processing
vs = []
for line in content:
	if 'not actual text to be translated' not in line:
		if len(line) > 3:
			v = line.replace(' := ', splitter)
			v = v.rstrip().lstrip()
			pair = v.split(splitter)

			if len(pair) == 2:
				text = pair[1].rstrip().lstrip()
				if not(' ' not in text and '_' in text):
					if 'zonecon' not in text:
						if 'CAM-' not in text:
							vs.append(text)
			else:
				vs.append(v)
r = vs[0:int(len(vs) / div)]
print len(r)
with open('out.txt', 'w') as f:
	f.write('\n'.join(r))

print os.path.getsize('out.txt')
