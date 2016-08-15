MVN_EXEC := $(shell which mvn 2>/dev/null)

SUBMODULES = build_utils
SUBTARGETS = $(patsubst %,%/.git,$(SUBMODULES))

UTILS_PATH := build_utils
TEMPLATES_PATH := .

# Name of the service
SERVICE_NAME := magista
# Service image default tag
SERVICE_IMAGE_TAG ?= $(shell git rev-parse HEAD)
# The tag for service image to be pushed with
SERVICE_IMAGE_PUSH_TAG ?= $(SERVICE_IMAGE_TAG)

# Base image for the service
BASE_IMAGE_NAME := service_java
BASE_IMAGE_TAG := c3b86f874108683ee6f0c02ebc907ef2cf94568c

BUILD_IMAGE_TAG := 530114ab63a7ff0379a2220169a0be61d3f7c64c

CALL_ANYWHERE := all submodules release package

# Hint: 'test' might be a candidate for CALL_W_CONTAINER-only target
CALL_W_CONTAINER := $(CALL_ANYWHERE)

CUTLINE = $(shell printf '=%.0s' $$(seq 1 80))

SETTINGS_XML ?= $$HOME/.m2/settings.xml
SETTINGSXML_CONT_PATH := /tmp/m2_settings_xml
SETTINGSXML_HOST_PATH = $(shell dirname $(SETTINGS_XML))
DOCKER_RUN_OPTS = -v $(SETTINGSXML_HOST_PATH):$(SETTINGSXML_CONT_PATH)
export SETTINGSXML_HOST_PATH
export SETTINGSXML_CONT_PATH

# Warning: will not work on the host (without wc_ or wdeps_)
ADD_SETTINGS_XML = --settings=$(SETTINGSXML_CONT_PATH)/settings.xml


.PHONY: $(CALL_W_CONTAINER) Dockerfile

all:
	@echo "Ok"

package:
	@echo "package"
	@echo $(CUTLINE)
	$(MVN_EXEC) clean package -P test $(ADD_SETTINGS_XML)
release:
	@echo "release"
	@echo $(CUTLINE)
	$(MVN_EXEC) clean deploy -P test $(ADD_SETTINGS_XML)

-include $(UTILS_PATH)/make_lib/utils_container.mk
-include $(UTILS_PATH)/make_lib/utils_image.mk
export SERVICE_IMAGE_NAME
export SERVICE_IMAGE_TAG
export SERVICE_NAME

$(SUBTARGETS): %/.git: %
	git submodule update --init $<
	touch $@

submodules: $(SUBTARGETS)
