MAIN = Main
OUT_DIR = out

# sources and libs
SOURCES = $(shell find src/** -name "*.java")
LIBS 	= $(shell find lib/** -name "*.jar" | tr '\n' ':' | sed 's/:$$//')

# compilation
JAVAC_FLAGS	= -Xlint:all -Werror $(JPPROJECT_FLAGS)
JAVA_FLAGS 	= --enable-native-access=ALL-UNNAMED
# Classpath for java (Linux/Mac)
CP = $(OUT_DIR):$(LIBS)
# CP = $(OUT_DIR);$(LIBS) | tr '\n' ':'# Uncomment for Windows

#libraries
EXTERNAL_DIR	= external
LIB_DIR			= lib

JLINE_VER = 3.25.1
JLINE_JAR = $(LIB_DIR)/jline-$(JLINE_VER).jar
JLINE_URL = https://repo1.maven.org/maven2/org/jline/jline/$(JLINE_VER)/jline-$(JLINE_VER).jar

#fixme: get a specific hash
EXTERNAL_EK_LIB_REPO	= $(EXTERNAL_DIR)/lib-java-ekrebs-v0
LIB_EK_URL	= git@github.com:M0rgenstern-ekrebs/lib-java-ekrebs-v0.git
LIB_EK_JAR	= $(LIB_DIR)/lib_ekrebsv0.jar

# colors
INFO = \033[1;34m
WARN = \033[1;33m
ERR = \033[1;31m
SUCC = \033[1;32m
RESET = \033[0m

#function
# Print info function
define print_info
	@echo "$(INFO)[INFO]$(RESET) $(1)$(RESET)"
endef

define print_debug
	@echo "$(INFO)[DEBUG]$(WARN) $(1)$(RESET)"
endef

define print_warn
	@echo "$(WARN)[WARN]$(WARN) $(1)$(RESET)"
endef

define print_err
	@echo "$(ERR)[ERR]$(WARN) $(1)$(RESET)"
endef

define print_success
	@echo "$(SUCC)[DONE]$(RESET) $(1)$(RESET)"
endef


# MAKEFILE
all: $(OUT_DIR)

$(OUT_DIR): | $(JLINE_JAR) $(LIB_EK_JAR)
	@$(call print_info, "Compiling Java sources...")
	mkdir -p $(OUT_DIR)
	javac $(JAVAC_FLAGS) -cp "$(CP)" -d $(OUT_DIR) $(SOURCES)
	@$(call print_success,"")

run: all
	@$(call print_info,"Running the program...")
	@echo "java $(JAVA_FLAGS) -cp \"$(CP)\" $(MAIN)"
	@echo "$(SUCC)[DONE]$(RESET) $(1)$(RESET)"
	@echo ""
	@java $(JAVA_FLAGS) -cp "$(CP)" $(MAIN)

clean:
	@$(call print_info,"Cleaning build directory...")
	rm -rf $(OUT_DIR)

re: clean all

clean_libs:
	rm -rf $(LIB_DIR)

clean_externals:
	rm -rf $(EXTERNAL_DIR)

flammenwerfer: clean clean_externals clean_libs
	@$(call print_warn,"flammenwerfer !")

achtung_printer:
	@$(call print_warn,"achtung !")

jawohl_printer:
	@$(call print_warn,"jawohl !")

achtung: achtung_printer flammenwerfer all

jawohl: jawohl_printer flammenwerfer run

rerun: re run

liblist:
	@$(call print_info,"Listing all JAR contents...")
	jar tf $(LIBS)


#making the LIBRARIES .jar


$(LIB_DIR):
	mkdir -p lib
	@$(call print_info,"getting Jline lib .jar")

$(EXTERNAL_DIR):
	mkdir -p external
	@$(call print_info,"getting Jline lib .jar")

$(JLINE_JAR): | $(LIB_DIR)
	wget -O $@ $(JLINE_URL)
	@$(call print_info,"getting Jline lib .jar")

$(EXTERNAL_EK_LIB_REPO): | $(LIB_DIR) $(EXTERNAL_DIR)
	git clone $(LIB_EK_URL) $(EXTERNAL_EK_LIB_REPO)
	@$(call print_info,"getting lib_ek repo")

$(LIB_EK_JAR): | $(EXTERNAL_EK_LIB_REPO)
	@$(call print_info,"building lib_ek .jar")
	$(MAKE) lib_ekrebsv0.jar -C $(EXTERNAL_EK_LIB_REPO)
	mv $(EXTERNAL_EK_LIB_REPO)/lib_ekrebsv0.jar $(LIB_DIR)/lib_ekrebsv0.jar

.PHONY: all run clean clean_externals clean_libs clear re rerun liblist flammenwerfer jawohl jawohl_printer achtung_printer
