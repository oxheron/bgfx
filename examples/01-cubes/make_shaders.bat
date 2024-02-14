echo Remove this when shaderc is set
exit /b :: Remove this as well
set "shaderc="
shaderc -i ../common -f fragment.sc -o fragment.bin -p s_5_0 --type fragment
shaderc -i ../common -f vertex.sc -o vertex.bin -p s_5_0 --type vertex
shaderc -i ../common -f fill_compute.sc -o fill_compute.bin -p s_5_0 --type compute
mv fragment.bin ../runtime/shaders/dx11/fragment.bin
mv vertex.bin ../runtime/shaders/dx11/vertex.bin
mv fill_compute.bin ../runtime/shaders/dx11/fill_compute.bin
