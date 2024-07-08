# DFA-and-PDA-modelling-system
This is a repository for my Computer Science BSc thesis. My thesis is a program for modelling deterministic finite automatons (DFA) and pushdown automatons (PDA).

The program supports both English and Hungarian. The default language is Hungarian but it can be easily set to English: use ``Ctrl + ,`` to open the settings and select 'angol' for the 'Nyelv' option then click 'Igen'. 

The thesis——in Hungarian——can be found here: https://edit.elte.hu/xmlui/handle/10831/56019


## Compile
``ant -noinput -buildfile build.xml compile``

## Run
``ant -noinput -buildfile build.xml run``

## Test
``ant -noinput -buildfile build.xml test``
