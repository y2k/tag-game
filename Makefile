.PHONY: start
start:
	lein figwheel

.PHONY: docker_image
docker_image:
	docker build . -t y2khub/tag_game -f .github/Dockerfile
