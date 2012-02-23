@echo off
pushd %1
for %%i in (*.*) do call lwrcase "%%i"
popd