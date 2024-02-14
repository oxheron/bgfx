/*
 * Copyright 2011-2024 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx/blob/master/LICENSE
 */

#ifndef BGFX_SHADER_H_HEADER_GUARD
#define BGFX_SHADER_H_HEADER_GUARD

#if !defined(BGFX_CONFIG_MAX_BONES)
#	define BGFX_CONFIG_MAX_BONES 32
#endif // !defined(BGFX_CONFIG_MAX_BONES)

#ifndef __cplusplus

#if BGFX_SHADER_LANGUAGE_HLSL > 300
#	define BRANCH [branch]
#	define LOOP   [loop]
#	define UNROLL [unroll]
#else
#	define BRANCH
#	define LOOP
#	define UNROLL
#endif // BGFX_SHADER_LANGUAGE_HLSL > 300

#if (BGFX_SHADER_LANGUAGE_HLSL > 300 || BGFX_SHADER_LANGUAGE_METAL || BGFX_SHADER_LANGUAGE_SPIRV) && BGFX_SHADER_TYPE_FRAGMENT
#	define EARLY_DEPTH_STENCIL [earlydepthstencil]
#else
#	define EARLY_DEPTH_STENCIL
#endif // BGFX_SHADER_LANGUAGE_HLSL > 300 && BGFX_SHADER_TYPE_FRAGMENT

#if BGFX_SHADER_LANGUAGE_GLSL
#	define ARRAY_BEGIN(_type, _name, _count) _type _name[_count] = _type[](
#	define ARRAY_END() )
#else
#	define ARRAY_BEGIN(_type, _name, _count) _type _name[_count] = {
#	define ARRAY_END() }
#endif // BGFX_SHADER_LANGUAGE_GLSL

#if BGFX_SHADER_LANGUAGE_HLSL \
 || BGFX_SHADER_LANGUAGE_PSSL \
 || BGFX_SHADER_LANGUAGE_SPIRV \
 || BGFX_SHADER_LANGUAGE_METAL
#	define CONST(_x) static const _x
#	define dFdx(_x) ddx(_x)
#	define dFdy(_y) ddy(-(_y))
#	define inversesqrt(_x) rsqrt(_x)
#	define fract(_x) frac(_x)

#	define bvec2 bool2
#	define bvec3 bool3
#	define bvec4 bool4

// To be able to patch the uav registers on the DXBC SPDB Chunk (D3D11 renderer) the whitespaces around
// '_type[_reg]' are necessary. This only affects shaders with debug info (i.e., those that have the SPDB Chunk).
#	if BGFX_SHADER_LANGUAGE_HLSL > 400 || BGFX_SHADER_LANGUAGE_PSSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL
#		define REGISTER(_type, _reg) register( _type[_reg] )
#	else
#		define REGISTER(_type, _reg) register(_type ## _reg)
#	endif // BGFX_SHADER_LANGUAGE_HLSL

#	if BGFX_SHADER_LANGUAGE_HLSL > 300 || BGFX_SHADER_LANGUAGE_PSSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL
#		if BGFX_SHADER_LANGUAGE_HLSL > 400 || BGFX_SHADER_LANGUAGE_PSSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL
#			define dFdxCoarse(_x) ddx_coarse(_x)
#			define dFdxFine(_x)   ddx_fine(_x)
#			define dFdyCoarse(_y) ddy_coarse(-(_y))
#			define dFdyFine(_y)   ddy_fine(-(_y))
#		endif // BGFX_SHADER_LANGUAGE_HLSL > 400

#		if BGFX_SHADER_LANGUAGE_HLSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL
float intBitsToFloat(int   _x) { return asfloat(_x); }
vec2  intBitsToFloat(uint2 _x) { return asfloat(_x); }
vec3  intBitsToFloat(uint3 _x) { return asfloat(_x); }
vec4  intBitsToFloat(uint4 _x) { return asfloat(_x); }
#		endif // BGFX_SHADER_LANGUAGE_HLSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL

float uintBitsToFloat(uint  _x) { return asfloat(_x); }
vec2  uintBitsToFloat(uint2 _x) { return asfloat(_x); }
vec3  uintBitsToFloat(uint3 _x) { return asfloat(_x); }
vec4  uintBitsToFloat(uint4 _x) { return asfloat(_x); }

uint  floatBitsToUint(float _x) { return asuint(_x); }
uvec2 floatBitsToUint(vec2  _x) { return asuint(_x); }
uvec3 floatBitsToUint(vec3  _x) { return asuint(_x); }
uvec4 floatBitsToUint(vec4  _x) { return asuint(_x); }

int   floatBitsToInt(float _x) { return asint(_x); }
ivec2 floatBitsToInt(vec2  _x) { return asint(_x); }
ivec3 floatBitsToInt(vec3  _x) { return asint(_x); }
ivec4 floatBitsToInt(vec4  _x) { return asint(_x); }

uint  bitfieldReverse(uint  _x) { return reversebits(_x); }
uint2 bitfieldReverse(uint2 _x) { return reversebits(_x); }
uint3 bitfieldReverse(uint3 _x) { return reversebits(_x); }
uint4 bitfieldReverse(uint4 _x) { return reversebits(_x); }

#		if !BGFX_SHADER_LANGUAGE_SPIRV
uint packHalf2x16(vec2 _x)
{
	return (f32tof16(_x.y)<<16) | f32tof16(_x.x);
}

vec2 unpackHalf2x16(uint _x)
{
	return vec2(f16tof32(_x & 0xffff), f16tof32(_x >> 16) );
}
#		endif // !BGFX_SHADER_LANGUAGE_SPIRV

struct BgfxSampler2D
{
	SamplerState m_sampler;
	Texture2D m_texture;
};

struct BgfxISampler2D
{
	Texture2D<ivec4> m_texture;
};

struct BgfxUSampler2D
{
	Texture2D<uvec4> m_texture;
};

struct BgfxSampler2DArray
{
	SamplerState m_sampler;
	Texture2DArray m_texture;
};

struct BgfxSampler2DShadow
{
	SamplerComparisonState m_sampler;
	Texture2D m_texture;
};

struct BgfxSampler2DArrayShadow
{
	SamplerComparisonState m_sampler;
	Texture2DArray m_texture;
};

struct BgfxSampler3D
{
	SamplerState m_sampler;
	Texture3D m_texture;
};

struct BgfxISampler3D
{
	Texture3D<ivec4> m_texture;
};

struct BgfxUSampler3D
{
	Texture3D<uvec4> m_texture;
};

struct BgfxSamplerCube
{
	SamplerState m_sampler;
	TextureCube m_texture;
};

struct BgfxSamplerCubeShadow
{
	SamplerComparisonState m_sampler;
	TextureCube m_texture;
};

struct BgfxSampler2DMS
{
	Texture2DMS<vec4> m_texture;
};

vec4 bgfxTexture2D(BgfxSampler2D _sampler, vec2 _coord)
{
	return _sampler.m_texture.Sample(_sampler.m_sampler, _coord);
}

vec4 bgfxTexture2DBias(BgfxSampler2D _sampler, vec2 _coord, float _bias)
{
	return _sampler.m_texture.SampleBias(_sampler.m_sampler, _coord, _bias);
}

vec4 bgfxTexture2DLod(BgfxSampler2D _sampler, vec2 _coord, float _level)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _level);
}

vec4 bgfxTexture2DLodOffset(BgfxSampler2D _sampler, vec2 _coord, float _level, ivec2 _offset)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _level, _offset);
}

vec4 bgfxTexture2DProj(BgfxSampler2D _sampler, vec3 _coord)
{
	vec2 coord = _coord.xy * rcp(_coord.z);
	return _sampler.m_texture.Sample(_sampler.m_sampler, coord);
}

vec4 bgfxTexture2DProj(BgfxSampler2D _sampler, vec4 _coord)
{
	vec2 coord = _coord.xy * rcp(_coord.w);
	return _sampler.m_texture.Sample(_sampler.m_sampler, coord);
}

vec4 bgfxTexture2DGrad(BgfxSampler2D _sampler, vec2 _coord, vec2 _dPdx, vec2 _dPdy)
{
	return _sampler.m_texture.SampleGrad(_sampler.m_sampler, _coord, _dPdx, _dPdy);
}

vec4 bgfxTexture2DArray(BgfxSampler2DArray _sampler, vec3 _coord)
{
	return _sampler.m_texture.Sample(_sampler.m_sampler, _coord);
}

vec4 bgfxTexture2DArrayLod(BgfxSampler2DArray _sampler, vec3 _coord, float _lod)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _lod);
}

vec4 bgfxTexture2DArrayLodOffset(BgfxSampler2DArray _sampler, vec3 _coord, float _level, ivec2 _offset)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _level, _offset);
}

float bgfxShadow2D(BgfxSampler2DShadow _sampler, vec3 _coord)
{
	return _sampler.m_texture.SampleCmpLevelZero(_sampler.m_sampler, _coord.xy, _coord.z);
}

float bgfxShadow2DProj(BgfxSampler2DShadow _sampler, vec4 _coord)
{
	vec3 coord = _coord.xyz * rcp(_coord.w);
	return _sampler.m_texture.SampleCmpLevelZero(_sampler.m_sampler, coord.xy, coord.z);
}

vec2 bgfxTextureSize(BgfxSampler2DShadow _sampler, int _lod)
{
	vec2 result;
	float numberOfMipMapLevels;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, numberOfMipMapLevels);
	return result;
}

vec4 bgfxShadow2DArray(BgfxSampler2DArrayShadow _sampler, vec4 _coord)
{
	return _sampler.m_texture.SampleCmpLevelZero(_sampler.m_sampler, _coord.xyz, _coord.w);
}

vec2 bgfxTextureSize(BgfxSampler2DArrayShadow _sampler, int _lod)
{
	vec2 result;
	float numberOfMipMapLevels;
	float numberOfElements;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, numberOfElements, numberOfMipMapLevels);
	return result;
}

vec4 bgfxTexture3D(BgfxSampler3D _sampler, vec3 _coord)
{
	return _sampler.m_texture.Sample(_sampler.m_sampler, _coord);
}

vec4 bgfxTexture3DLod(BgfxSampler3D _sampler, vec3 _coord, float _level)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _level);
}

ivec4 bgfxTexture3D(BgfxISampler3D _sampler, vec3 _coord)
{
	uvec3 size;
	_sampler.m_texture.GetDimensions(size.x, size.y, size.z);
	return _sampler.m_texture.Load(ivec4(_coord * size, 0) );
}

uvec4 bgfxTexture3D(BgfxUSampler3D _sampler, vec3 _coord)
{
	uvec3 size;
	_sampler.m_texture.GetDimensions(size.x, size.y, size.z);
	return _sampler.m_texture.Load(ivec4(_coord * size, 0) );
}

vec4 bgfxTextureCube(BgfxSamplerCube _sampler, vec3 _coord)
{
	return _sampler.m_texture.Sample(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureCubeBias(BgfxSamplerCube _sampler, vec3 _coord, float _bias)
{
	return _sampler.m_texture.SampleBias(_sampler.m_sampler, _coord, _bias);
}

vec4 bgfxTextureCubeLod(BgfxSamplerCube _sampler, vec3 _coord, float _level)
{
	return _sampler.m_texture.SampleLevel(_sampler.m_sampler, _coord, _level);
}

float bgfxShadowCube(BgfxSamplerCubeShadow _sampler, vec4 _coord)
{
	return _sampler.m_texture.SampleCmpLevelZero(_sampler.m_sampler, _coord.xyz, _coord.w);
}

vec4 bgfxTexelFetch(BgfxSampler2D _sampler, ivec2 _coord, int _lod)
{
	return _sampler.m_texture.Load(ivec3(_coord, _lod) );
}

vec4 bgfxTexelFetchOffset(BgfxSampler2D _sampler, ivec2 _coord, int _lod, ivec2 _offset)
{
	return _sampler.m_texture.Load(ivec3(_coord, _lod), _offset );
}

vec2 bgfxTextureSize(BgfxSampler2D _sampler, int _lod)
{
	vec2 result;
	float numberOfMipMapLevels;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, numberOfMipMapLevels);
	return result;
}

vec2 bgfxTextureSize(BgfxISampler2D _sampler, int _lod)
{
	vec2 result;
	float numberOfMipMapLevels;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, numberOfMipMapLevels);
	return result;
}

vec2 bgfxTextureSize(BgfxUSampler2D _sampler, int _lod)
{
	vec2 result;
	float numberOfMipMapLevels;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, numberOfMipMapLevels);
	return result;
}

vec4 bgfxTextureGather0(BgfxSampler2D _sampler, vec2 _coord)
{
	return _sampler.m_texture.GatherRed(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather1(BgfxSampler2D _sampler, vec2 _coord)
{
	return _sampler.m_texture.GatherGreen(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather2(BgfxSampler2D _sampler, vec2 _coord)
{
	return _sampler.m_texture.GatherBlue(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather3(BgfxSampler2D _sampler, vec2 _coord)
{
	return _sampler.m_texture.GatherAlpha(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGatherOffset0(BgfxSampler2D _sampler, vec2 _coord, ivec2 _offset)
{
	return _sampler.m_texture.GatherRed(_sampler.m_sampler, _coord, _offset);
}

vec4 bgfxTextureGatherOffset1(BgfxSampler2D _sampler, vec2 _coord, ivec2 _offset)
{
	return _sampler.m_texture.GatherGreen(_sampler.m_sampler, _coord, _offset);
}

vec4 bgfxTextureGatherOffset2(BgfxSampler2D _sampler, vec2 _coord, ivec2 _offset)
{
	return _sampler.m_texture.GatherBlue(_sampler.m_sampler, _coord, _offset);
}

vec4 bgfxTextureGatherOffset3(BgfxSampler2D _sampler, vec2 _coord, ivec2 _offset)
{
	return _sampler.m_texture.GatherAlpha(_sampler.m_sampler, _coord, _offset);
}

vec4 bgfxTextureGather0(BgfxSampler2DArray _sampler, vec3 _coord)
{
	return _sampler.m_texture.GatherRed(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather1(BgfxSampler2DArray _sampler, vec3 _coord)
{
	return _sampler.m_texture.GatherGreen(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather2(BgfxSampler2DArray _sampler, vec3 _coord)
{
	return _sampler.m_texture.GatherBlue(_sampler.m_sampler, _coord);
}

vec4 bgfxTextureGather3(BgfxSampler2DArray _sampler, vec3 _coord)
{
	return _sampler.m_texture.GatherAlpha(_sampler.m_sampler, _coord);
}

ivec4 bgfxTexelFetch(BgfxISampler2D _sampler, ivec2 _coord, int _lod)
{
	return _sampler.m_texture.Load(ivec3(_coord, _lod) );
}

uvec4 bgfxTexelFetch(BgfxUSampler2D _sampler, ivec2 _coord, int _lod)
{
	return _sampler.m_texture.Load(ivec3(_coord, _lod) );
}

vec4 bgfxTexelFetch(BgfxSampler2DMS _sampler, ivec2 _coord, int _sampleIdx)
{
	return _sampler.m_texture.Load(_coord, _sampleIdx);
}

vec4 bgfxTexelFetch(BgfxSampler2DArray _sampler, ivec3 _coord, int _lod)
{
	return _sampler.m_texture.Load(ivec4(_coord, _lod) );
}

vec4 bgfxTexelFetch(BgfxSampler3D _sampler, ivec3 _coord, int _lod)
{
	return _sampler.m_texture.Load(ivec4(_coord, _lod) );
}

vec3 bgfxTextureSize(BgfxSampler3D _sampler, int _lod)
{
	vec3 result;
	float numberOfMipMapLevels;
	_sampler.m_texture.GetDimensions(_lod, result.x, result.y, result.z, numberOfMipMapLevels);
	return result;
}

#		define SAMPLER2D(_name, _reg) \
			uniform SamplerState _name ## Sampler : REGISTER(s, _reg); \
			uniform Texture2D _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler2D _name = { _name ## Sampler, _name ## Texture }
#		define ISAMPLER2D(_name, _reg) \
			uniform Texture2D<ivec4> _name ## Texture : REGISTER(t, _reg); \
			static BgfxISampler2D _name = { _name ## Texture }
#		define USAMPLER2D(_name, _reg) \
			uniform Texture2D<uvec4> _name ## Texture : REGISTER(t, _reg); \
			static BgfxUSampler2D _name = { _name ## Texture }
#		define sampler2D BgfxSampler2D
#		define texture2D(_sampler, _coord) bgfxTexture2D(_sampler, _coord)
#		define texture2DBias(_sampler, _coord, _bias) bgfxTexture2DBias(_sampler, _coord, _bias)
#		define texture2DLod(_sampler, _coord, _level) bgfxTexture2DLod(_sampler, _coord, _level)
#		define texture2DLodOffset(_sampler, _coord, _level, _offset) bgfxTexture2DLodOffset(_sampler, _coord, _level, _offset)
#		define texture2DProj(_sampler, _coord) bgfxTexture2DProj(_sampler, _coord)
#		define texture2DGrad(_sampler, _coord, _dPdx, _dPdy) bgfxTexture2DGrad(_sampler, _coord, _dPdx, _dPdy)

#		define SAMPLER2DARRAY(_name, _reg) \
			uniform SamplerState _name ## Sampler : REGISTER(s, _reg); \
			uniform Texture2DArray _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler2DArray _name = { _name ## Sampler, _name ## Texture }
#		define sampler2DArray BgfxSampler2DArray
#		define texture2DArray(_sampler, _coord) bgfxTexture2DArray(_sampler, _coord)
#		define texture2DArrayLod(_sampler, _coord, _lod) bgfxTexture2DArrayLod(_sampler, _coord, _lod)
#		define texture2DArrayLodOffset(_sampler, _coord, _level, _offset) bgfxTexture2DArrayLodOffset(_sampler, _coord, _level, _offset)

#		define SAMPLER2DMS(_name, _reg) \
			uniform Texture2DMS<vec4> _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler2DMS _name = { _name ## Texture }
#		define sampler2DMS BgfxSampler2DMS

#		define SAMPLER2DSHADOW(_name, _reg) \
			uniform SamplerComparisonState _name ## SamplerComparison : REGISTER(s, _reg); \
			uniform Texture2D _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler2DShadow _name = { _name ## SamplerComparison, _name ## Texture }
#		define sampler2DShadow BgfxSampler2DShadow
#		define shadow2D(_sampler, _coord) bgfxShadow2D(_sampler, _coord)
#		define shadow2DProj(_sampler, _coord) bgfxShadow2DProj(_sampler, _coord)

#		define SAMPLER2DARRAYSHADOW(_name, _reg) \
			uniform SamplerComparisonState _name ## SamplerComparison : REGISTER(s, _reg); \
			uniform Texture2DArray _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler2DArrayShadow _name = { _name ## SamplerComparison, _name ## Texture }
#		define sampler2DArrayShadow BgfxSampler2DArrayShadow
#		define shadow2DArray(_sampler, _coord) bgfxShadow2DArray(_sampler, _coord)

#		define SAMPLER3D(_name, _reg) \
			uniform SamplerState _name ## Sampler : REGISTER(s, _reg); \
			uniform Texture3D _name ## Texture : REGISTER(t, _reg); \
			static BgfxSampler3D _name = { _name ## Sampler, _name ## Texture }
#		define ISAMPLER3D(_name, _reg) \
			uniform Texture3D<ivec4> _name ## Texture : REGISTER(t, _reg); \
			static BgfxISampler3D _name = { _name ## Texture }
#		define USAMPLER3D(_name, _reg) \
			uniform Texture3D<uvec4> _name ## Texture : REGISTER(t, _reg); \
			static BgfxUSampler3D _name = { _name ## Texture }
#		define sampler3D BgfxSampler3D
#		define texture3D(_sampler, _coord) bgfxTexture3D(_sampler, _coord)
#		define texture3DLod(_sampler, _coord, _level) bgfxTexture3DLod(_sampler, _coord, _level)

#		define SAMPLERCUBE(_name, _reg) \
			uniform SamplerState _name ## Sampler : REGISTER(s, _reg); \
			uniform TextureCube _name ## Texture : REGISTER(t, _reg); \
			static BgfxSamplerCube _name = { _name ## Sampler, _name ## Texture }
#		define samplerCube BgfxSamplerCube
#		define textureCube(_sampler, _coord) bgfxTextureCube(_sampler, _coord)
#		define textureCubeBias(_sampler, _coord, _bias) bgfxTextureCubeBias(_sampler, _coord, _bias)
#		define textureCubeLod(_sampler, _coord, _level) bgfxTextureCubeLod(_sampler, _coord, _level)

#		define SAMPLERCUBESHADOW(_name, _reg) \
			uniform SamplerComparisonState _name ## SamplerComparison : REGISTER(s, _reg); \
			uniform TextureCube _name ## Texture : REGISTER(t, _reg); \
			static BgfxSamplerCubeShadow _name = { _name ## SamplerComparison, _name ## Texture }
#		define samplerCubeShadow BgfxSamplerCubeShadow
#		define shadowCube(_sampler, _coord) bgfxShadowCube(_sampler, _coord)

#		define texelFetch(_sampler, _coord, _lod) bgfxTexelFetch(_sampler, _coord, _lod)
#		define texelFetchOffset(_sampler, _coord, _lod, _offset) bgfxTexelFetchOffset(_sampler, _coord, _lod, _offset)
#		define textureSize(_sampler, _lod) bgfxTextureSize(_sampler, _lod)
#		define textureGather(_sampler, _coord, _comp) bgfxTextureGather ## _comp(_sampler, _coord)
#		define textureGatherOffset(_sampler, _coord, _offset, _comp) bgfxTextureGatherOffset ## _comp(_sampler, _coord, _offset)
#	else

#		define sampler2DShadow sampler2D

vec4 bgfxTexture2DProj(sampler2D _sampler, vec3 _coord)
{
	return tex2Dproj(_sampler, vec4(_coord.xy, 0.0, _coord.z) );
}

vec4 bgfxTexture2DProj(sampler2D _sampler, vec4 _coord)
{
	return tex2Dproj(_sampler, _coord);
}

float bgfxShadow2D(sampler2DShadow _sampler, vec3 _coord)
{
#if 0
	float occluder = tex2D(_sampler, _coord.xy).x;
	return step(_coord.z, occluder);
#else
	return tex2Dproj(_sampler, vec4(_coord.xy, _coord.z, 1.0) ).x;
#endif // 0
}

float bgfxShadow2DProj(sampler2DShadow _sampler, vec4 _coord)
{
#if 0
	vec3 coord = _coord.xyz * rcp(_coord.w);
	float occluder = tex2D(_sampler, coord.xy).x;
	return step(coord.z, occluder);
#else
	return tex2Dproj(_sampler, _coord).x;
#endif // 0
}

#		define SAMPLER2D(_name, _reg) uniform sampler2D _name : REGISTER(s, _reg)
#		define SAMPLER2DMS(_name, _reg) uniform sampler2DMS _name : REGISTER(s, _reg)
#		define texture2D(_sampler, _coord) tex2D(_sampler, _coord)
#		define texture2DProj(_sampler, _coord) bgfxTexture2DProj(_sampler, _coord)

#		define SAMPLER2DARRAY(_name, _reg) SAMPLER2D(_name, _reg)
#		define texture2DArray(_sampler, _coord) texture2D(_sampler, (_coord).xy)
#		define texture2DArrayLod(_sampler, _coord, _lod) texture2DLod(_sampler, _coord, _lod)

#		define SAMPLER2DSHADOW(_name, _reg) uniform sampler2DShadow _name : REGISTER(s, _reg)
#		define shadow2D(_sampler, _coord) bgfxShadow2D(_sampler, _coord)
#		define shadow2DProj(_sampler, _coord) bgfxShadow2DProj(_sampler, _coord)

#		define SAMPLER3D(_name, _reg) uniform sampler3D _name : REGISTER(s, _reg)
#		define texture3D(_sampler, _coord) tex3D(_sampler, _coord)

#		define SAMPLERCUBE(_name, _reg) uniform samplerCUBE _name : REGISTER(s, _reg)
#		define textureCube(_sampler, _coord) texCUBE(_sampler, _coord)

#		define texture2DLod(_sampler, _coord, _level) tex2Dlod(_sampler, vec4( (_coord).xy, 0.0, _level) )
#		define texture2DGrad(_sampler, _coord, _dPdx, _dPdy) tex2Dgrad(_sampler, _coord, _dPdx, _dPdy)
#		define texture3DLod(_sampler, _coord, _level) tex3Dlod(_sampler, vec4( (_coord).xyz, _level) )
#		define textureCubeLod(_sampler, _coord, _level) texCUBElod(_sampler, vec4( (_coord).xyz, _level) )

#	endif // BGFX_SHADER_LANGUAGE_HLSL > 300

vec3 instMul(vec3 _vec, mat3 _mtx) { return mul(_mtx, _vec); }
vec3 instMul(mat3 _mtx, vec3 _vec) { return mul(_vec, _mtx); }
vec4 instMul(vec4 _vec, mat4 _mtx) { return mul(_mtx, _vec); }
vec4 instMul(mat4 _mtx, vec4 _vec) { return mul(_vec, _mtx); }

bvec2 lessThan(vec2 _a, vec2 _b) { return _a < _b; }
bvec3 lessThan(vec3 _a, vec3 _b) { return _a < _b; }
bvec4 lessThan(vec4 _a, vec4 _b) { return _a < _b; }

bvec2 lessThanEqual(vec2 _a, vec2 _b) { return _a <= _b; }
bvec3 lessThanEqual(vec3 _a, vec3 _b) { return _a <= _b; }
bvec4 lessThanEqual(vec4 _a, vec4 _b) { return _a <= _b; }

bvec2 greaterThan(vec2 _a, vec2 _b) { return _a > _b; }
bvec3 greaterThan(vec3 _a, vec3 _b) { return _a > _b; }
bvec4 greaterThan(vec4 _a, vec4 _b) { return _a > _b; }

bvec2 greaterThanEqual(vec2 _a, vec2 _b) { return _a >= _b; }
bvec3 greaterThanEqual(vec3 _a, vec3 _b) { return _a >= _b; }
bvec4 greaterThanEqual(vec4 _a, vec4 _b) { return _a >= _b; }

bvec2 notEqual(vec2 _a, vec2 _b) { return _a != _b; }
bvec3 notEqual(vec3 _a, vec3 _b) { return _a != _b; }
bvec4 notEqual(vec4 _a, vec4 _b) { return _a != _b; }

bvec2 equal(vec2 _a, vec2 _b) { return _a == _b; }
bvec3 equal(vec3 _a, vec3 _b) { return _a == _b; }
bvec4 equal(vec4 _a, vec4 _b) { return _a == _b; }

float mix(float _a, float _b, float _t) { return lerp(_a, _b, _t); }
vec2  mix(vec2  _a, vec2  _b, vec2  _t) { return lerp(_a, _b, _t); }
vec3  mix(vec3  _a, vec3  _b, vec3  _t) { return lerp(_a, _b, _t); }
vec4  mix(vec4  _a, vec4  _b, vec4  _t) { return lerp(_a, _b, _t); }

float mod(float _a, float _b) { return _a - _b * floor(_a / _b); }
vec2  mod(vec2  _a, vec2  _b) { return _a - _b * floor(_a / _b); }
vec3  mod(vec3  _a, vec3  _b) { return _a - _b * floor(_a / _b); }
vec4  mod(vec4  _a, vec4  _b) { return _a - _b * floor(_a / _b); }

#else
#	define CONST(_x) const _x
#	define atan2(_x, _y) atan(_x, _y)
#	define mul(_a, _b) ( (_a) * (_b) )
#	define saturate(_x) clamp(_x, 0.0, 1.0)
#	define SAMPLER2D(_name, _reg)       uniform sampler2D _name
#	define SAMPLER2DMS(_name, _reg)     uniform sampler2DMS _name
#	define SAMPLER3D(_name, _reg)       uniform sampler3D _name
#	define SAMPLERCUBE(_name, _reg)     uniform samplerCube _name
#	define SAMPLER2DSHADOW(_name, _reg) uniform sampler2DShadow _name

#	define SAMPLER2DARRAY(_name, _reg)       uniform sampler2DArray _name
#	define SAMPLER2DMSARRAY(_name, _reg)     uniform sampler2DMSArray _name
#	define SAMPLERCUBEARRAY(_name, _reg)     uniform samplerCubeArray _name
#	define SAMPLER2DARRAYSHADOW(_name, _reg) uniform sampler2DArrayShadow _name

#	define ISAMPLER2D(_name, _reg) uniform isampler2D _name
#	define USAMPLER2D(_name, _reg) uniform usampler2D _name
#	define ISAMPLER3D(_name, _reg) uniform isampler3D _name
#	define USAMPLER3D(_name, _reg) uniform usampler3D _name

#	if BGFX_SHADER_LANGUAGE_GLSL >= 130
#		define texture2D(_sampler, _coord)      texture(_sampler, _coord)
#		define texture2DArray(_sampler, _coord) texture(_sampler, _coord)
#		define texture3D(_sampler, _coord)      texture(_sampler, _coord)
#		define textureCube(_sampler, _coord)    texture(_sampler, _coord)
#		define texture2DLod(_sampler, _coord, _lod)                textureLod(_sampler, _coord, _lod)
#		define texture2DLodOffset(_sampler, _coord, _lod, _offset) textureLodOffset(_sampler, _coord, _lod, _offset)
#		define texture2DBias(_sampler, _coord, _bias)      texture(_sampler, _coord, _bias)
#		define textureCubeBias(_sampler, _coord, _bias)    texture(_sampler, _coord, _bias)
#	else
#		define texture2DBias(_sampler, _coord, _bias)      texture2D(_sampler, _coord, _bias)
#		define textureCubeBias(_sampler, _coord, _bias)    textureCube(_sampler, _coord, _bias)
#	endif // BGFX_SHADER_LANGUAGE_GLSL >= 130

vec3 instMul(vec3 _vec, mat3 _mtx) { return mul(_vec, _mtx); }
vec3 instMul(mat3 _mtx, vec3 _vec) { return mul(_mtx, _vec); }
vec4 instMul(vec4 _vec, mat4 _mtx) { return mul(_vec, _mtx); }
vec4 instMul(mat4 _mtx, vec4 _vec) { return mul(_mtx, _vec); }

float rcp(float _a) { return 1.0/_a; }
vec2  rcp(vec2  _a) { return vec2(1.0)/_a; }
vec3  rcp(vec3  _a) { return vec3(1.0)/_a; }
vec4  rcp(vec4  _a) { return vec4(1.0)/_a; }
#endif // BGFX_SHADER_LANGUAGE_*

vec2 vec2_splat(float _x) { return vec2(_x, _x); }
vec3 vec3_splat(float _x) { return vec3(_x, _x, _x); }
vec4 vec4_splat(float _x) { return vec4(_x, _x, _x, _x); }

#if BGFX_SHADER_LANGUAGE_GLSL >= 130 || BGFX_SHADER_LANGUAGE_HLSL || BGFX_SHADER_LANGUAGE_PSSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL
uvec2 uvec2_splat(uint _x) { return uvec2(_x, _x); }
uvec3 uvec3_splat(uint _x) { return uvec3(_x, _x, _x); }
uvec4 uvec4_splat(uint _x) { return uvec4(_x, _x, _x, _x); }
#endif // BGFX_SHADER_LANGUAGE_GLSL >= 130 || BGFX_SHADER_LANGUAGE_HLSL || BGFX_SHADER_LANGUAGE_PSSL || BGFX_SHADER_LANGUAGE_SPIRV || BGFX_SHADER_LANGUAGE_METAL

mat4 mtxFromRows(vec4 _0, vec4 _1, vec4 _2, vec4 _3)
{
#if BGFX_SHADER_LANGUAGE_GLSL
	return transpose(mat4(_0, _1, _2, _3) );
#else
	return mat4(_0, _1, _2, _3);
#endif // BGFX_SHADER_LANGUAGE_GLSL
}
mat4 mtxFromCols(vec4 _0, vec4 _1, vec4 _2, vec4 _3)
{
#if BGFX_SHADER_LANGUAGE_GLSL
	return mat4(_0, _1, _2, _3);
#else
	return transpose(mat4(_0, _1, _2, _3) );
#endif // BGFX_SHADER_LANGUAGE_GLSL
}
mat3 mtxFromRows(vec3 _0, vec3 _1, vec3 _2)
{
#if BGFX_SHADER_LANGUAGE_GLSL
	return transpose(mat3(_0, _1, _2) );
#else
	return mat3(_0, _1, _2);
#endif // BGFX_SHADER_LANGUAGE_GLSL
}
mat3 mtxFromCols(vec3 _0, vec3 _1, vec3 _2)
{
#if BGFX_SHADER_LANGUAGE_GLSL
	return mat3(_0, _1, _2);
#else
	return transpose(mat3(_0, _1, _2) );
#endif // BGFX_SHADER_LANGUAGE_GLSL
}

#if BGFX_SHADER_LANGUAGE_GLSL
#define mtxFromRows3(_0, _1, _2)     transpose(mat3(_0, _1, _2) )
#define mtxFromRows4(_0, _1, _2, _3) transpose(mat4(_0, _1, _2, _3) )
#define mtxFromCols3(_0, _1, _2)               mat3(_0, _1, _2)
#define mtxFromCols4(_0, _1, _2, _3)           mat4(_0, _1, _2, _3)
#else
#define mtxFromRows3(_0, _1, _2)               mat3(_0, _1, _2)
#define mtxFromRows4(_0, _1, _2, _3)           mat4(_0, _1, _2, _3)
#define mtxFromCols3(_0, _1, _2)     transpose(mat3(_0, _1, _2) )
#define mtxFromCols4(_0, _1, _2, _3) transpose(mat4(_0, _1, _2, _3) )
#endif // BGFX_SHADER_LANGUAGE_GLSL

uniform vec4  u_viewRect;
uniform vec4  u_viewTexel;
uniform mat4  u_view;
uniform mat4  u_invView;
uniform mat4  u_proj;
uniform mat4  u_invProj;
uniform mat4  u_viewProj;
uniform mat4  u_invViewProj;
uniform mat4  u_model[BGFX_CONFIG_MAX_BONES];
uniform mat4  u_modelView;
uniform mat4  u_modelViewProj;
uniform vec4  u_alphaRef4;
#define u_alphaRef u_alphaRef4.x

#endif // __cplusplus

#endif // BGFX_SHADER_H_HEADER_GUARD


#ifndef BGFX_COMPUTE_H_HEADER_GUARD
#define BGFX_COMPUTE_H_HEADER_GUARD

#ifndef __cplusplus

#if BGFX_SHADER_LANGUAGE_HLSL > 0 && BGFX_SHADER_LANGUAGE_HLSL < 400
#	error "Compute is not supported!"
#endif // BGFX_SHADER_LANGUAGE_HLSL

#if BGFX_SHADER_LANGUAGE_METAL || BGFX_SHADER_LANGUAGE_SPIRV
#	define FORMAT(_format) [[spv::format_ ## _format]]
#	define WRITEONLY [[spv::nonreadable]]
#else
#	define FORMAT(_format)
#	define WRITEONLY
#endif // BGFX_SHADER_LANGUAGE_METAL || BGFX_SHADER_LANGUAGE_SPIRV

#if BGFX_SHADER_LANGUAGE_GLSL

#define SHARED shared

#define __IMAGE_XX(_name, _format, _reg, _image, _access) \
	layout(_format, binding=_reg) _access uniform highp _image _name

#define readwrite
#define IMAGE2D_RO( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2D,  readonly)
#define UIMAGE2D_RO(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2D, readonly)
#define IMAGE2D_WR( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2D,  writeonly)
#define UIMAGE2D_WR(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2D, writeonly)
#define IMAGE2D_RW( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2D,  readwrite)
#define UIMAGE2D_RW(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2D, readwrite)

#define IMAGE2D_ARRAY_RO( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2DArray,  readonly)
#define UIMAGE2D_ARRAY_RO(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2DArray, readonly)
#define IMAGE2D_ARRAY_WR( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2DArray,  writeonly)
#define UIMAGE2D_ARRAY_WR(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2DArray, writeonly)
#define IMAGE2D_ARRAY_RW( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image2DArray,  readwrite)
#define UIMAGE2D_ARRAY_RW(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage2DArray, readwrite)

#define IMAGE3D_RO( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image3D,  readonly)
#define UIMAGE3D_RO(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage3D, readonly)
#define IMAGE3D_WR( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image3D,  writeonly)
#define UIMAGE3D_WR(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage3D, writeonly)
#define IMAGE3D_RW( _name, _format, _reg) __IMAGE_XX(_name, _format, _reg, image3D,  readwrite)
#define UIMAGE3D_RW(_name, _format, _reg) __IMAGE_XX(_name, _format, _reg, uimage3D, readwrite)

#define __BUFFER_XX(_name, _type, _reg, _access)                \
	layout(std430, binding=_reg) _access buffer _name ## Buffer \
	{                                                           \
		_type _name[];                                          \
	}

#define BUFFER_RO(_name, _type, _reg) __BUFFER_XX(_name, _type, _reg, readonly)
#define BUFFER_RW(_name, _type, _reg) __BUFFER_XX(_name, _type, _reg, readwrite)
#define BUFFER_WR(_name, _type, _reg) __BUFFER_XX(_name, _type, _reg, writeonly)

#define NUM_THREADS(_x, _y, _z) layout (local_size_x = _x, local_size_y = _y, local_size_z = _z) in;

#define atomicFetchAndAdd(_mem, _data, _original)                    _original = atomicAdd(_mem, _data)
#define atomicFetchAndAnd(_mem, _data, _original)                    _original = atomicAnd(_mem, _data)
#define atomicFetchAndMax(_mem, _data, _original)                    _original = atomicMax(_mem, _data)
#define atomicFetchAndMin(_mem, _data, _original)                    _original = atomicMin(_mem, _data)
#define atomicFetchAndOr(_mem, _data, _original)                     _original = atomicOr(_mem, _data)
#define atomicFetchAndXor(_mem, _data, _original)                    _original = atomicXor(_mem, _data)
#define atomicFetchAndExchange(_mem, _data, _original)               _original = atomicExchange(_mem, _data)
#define atomicFetchCompareExchange(_mem, _compare, _data, _original) _original = atomicCompSwap(_mem,_compare, _data)

#else

#define SHARED groupshared

#define COMP_r32ui    uint
#define COMP_rg32ui   uint2
#define COMP_rgba32ui uint4
#define COMP_r32f     float
#define COMP_r16f     float
#define COMP_rg16f    float2
#define COMP_rgba16f  float4
#if BGFX_SHADER_LANGUAGE_HLSL
#	define COMP_rgba8 unorm float4
#	define COMP_rg8   unorm float2
#	define COMP_r8    unorm float
#else
#	define COMP_rgba8       float4
#	define COMP_rg8         float2
#	define COMP_r8          float
#endif // BGFX_SHADER_LANGUAGE_HLSL
#define COMP_rgba32f  float4

#define IMAGE2D_RO( _name, _format, _reg)                                       \
	FORMAT(_format) Texture2D<COMP_ ## _format> _name : REGISTER(t, _reg);      \

#define UIMAGE2D_RO(_name, _format, _reg) IMAGE2D_RO(_name, _format, _reg)

#define IMAGE2D_WR( _name, _format, _reg)                                                 \
	WRITEONLY FORMAT(_format) RWTexture2D<COMP_ ## _format> _name : REGISTER(u, _reg);  \

#define UIMAGE2D_WR(_name, _format, _reg) IMAGE2D_WR(_name, _format, _reg)

#define IMAGE2D_RW( _name, _format, _reg)                            \
	FORMAT(_format) RWTexture2D<COMP_ ## _format> _name : REGISTER(u, _reg);  \

#define UIMAGE2D_RW(_name, _format, _reg) IMAGE2D_RW(_name, _format, _reg)

#define IMAGE2D_ARRAY_RO(_name, _format, _reg)                                     \
	FORMAT(_format) Texture2DArray<COMP_ ## _format> _name : REGISTER(t, _reg);    \

#define UIMAGE2D_ARRAY_RO(_name, _format, _reg) IMAGE2D_ARRAY_RO(_name, _format, _reg)

#define IMAGE2D_ARRAY_WR( _name, _format, _reg)                                       \
	WRITEONLY FORMAT(_format) RWTexture2DArray<COMP_ ## _format> _name : REGISTER(u, _reg);    \

#define UIMAGE2D_ARRAY_WR(_name, _format, _reg) IMAGE2D_ARRAY_WR(_name, _format, _reg)

#define IMAGE2D_ARRAY_RW(_name, _format, _reg)                              \
	FORMAT(_format) RWTexture2DArray<COMP_ ## _format> _name : REGISTER(u, _reg);    \

#define UIMAGE2D_ARRAY_RW(_name, _format, _reg) IMAGE2D_ARRAY_RW(_name, _format, _reg)

#define IMAGE3D_RO( _name, _format, _reg)                                     \
	FORMAT(_format) Texture3D<COMP_ ## _format> _name : REGISTER(t, _reg);

#define UIMAGE3D_RO(_name, _format, _reg) IMAGE3D_RO(_name, _format, _reg)

#define IMAGE3D_WR( _name, _format, _reg)                                      \
	WRITEONLY FORMAT(_format) RWTexture3D<COMP_ ## _format> _name : REGISTER(u, _reg);

#define UIMAGE3D_WR(_name, _format, _reg) IMAGE3D_RW(_name, _format, _reg)

#define IMAGE3D_RW( _name, _format, _reg)                            \
	FORMAT(_format) RWTexture3D<COMP_ ## _format> _name : REGISTER(u, _reg);  \

#define UIMAGE3D_RW(_name, _format, _reg) IMAGE3D_RW(_name, _format, _reg)

#if BGFX_SHADER_LANGUAGE_METAL || BGFX_SHADER_LANGUAGE_SPIRV
#define BUFFER_RO(_name, _struct, _reg) StructuredBuffer<_struct>   _name : REGISTER(t, _reg)
#define BUFFER_RW(_name, _struct, _reg) RWStructuredBuffer <_struct> _name : REGISTER(u, _reg)
#define BUFFER_WR(_name, _struct, _reg) BUFFER_RW(_name, _struct, _reg)
#else
#define BUFFER_RO(_name, _struct, _reg) Buffer<_struct>   _name : REGISTER(t, _reg)
#define BUFFER_RW(_name, _struct, _reg) RWBuffer<_struct> _name : REGISTER(u, _reg)
#define BUFFER_WR(_name, _struct, _reg) BUFFER_RW(_name, _struct, _reg)
#endif

#define NUM_THREADS(_x, _y, _z) [numthreads(_x, _y, _z)]

#define __IMAGE_IMPL_A(_format, _storeComponents, _type, _loadComponents)       \
	_type imageLoad(Texture2D<_format> _image, ivec2 _uv)                       \
	{                                                                           \
		return _image[_uv]._loadComponents;                                     \
	}                                                                           \
	\
	ivec2 imageSize(Texture2D<_format> _image)                                  \
	{                                                                           \
		uvec2 result;                                                           \
		_image.GetDimensions(result.x, result.y);                               \
		return ivec2(result);                                                   \
	}                                                                           \
	\
	_type imageLoad(RWTexture2D<_format> _image, ivec2 _uv)                     \
	{                                                                           \
		return _image[_uv]._loadComponents;                                     \
	}                                                                           \
	\
	void imageStore(RWTexture2D<_format> _image, ivec2 _uv,  _type _value)      \
	{                                                                           \
		_image[_uv] = _value._storeComponents;                                  \
	}                                                                           \
	\
	ivec2 imageSize(RWTexture2D<_format> _image)                                \
	{                                                                           \
		uvec2 result;                                                           \
		_image.GetDimensions(result.x, result.y);                               \
		return ivec2(result);                                                   \
	}                                                                           \
	\
	_type imageLoad(Texture2DArray<_format> _image, ivec3 _uvw)                 \
	{                                                                           \
		return _image[_uvw]._loadComponents;                                    \
	}                                                                           \
	\
	ivec3 imageSize(Texture2DArray<_format> _image)                             \
	{                                                                           \
		uvec3 result;                                                           \
		_image.GetDimensions(result.x, result.y, result.z);                     \
		return ivec3(result);                                                   \
	}                                                                           \
	\
	_type imageLoad(RWTexture2DArray<_format> _image, ivec3 _uvw)               \
	{                                                                           \
		return _image[_uvw]._loadComponents;                                    \
	}                                                                           \
	\
	void imageStore(RWTexture2DArray<_format> _image, ivec3 _uvw, _type _value) \
	{                                                                           \
		_image[_uvw] = _value._storeComponents;                                 \
	}                                                                           \
	\
	ivec3 imageSize(RWTexture2DArray<_format> _image)                           \
	{                                                                           \
		uvec3 result;                                                           \
		_image.GetDimensions(result.x, result.y, result.z);                     \
		return ivec3(result);                                                   \
	}                                                                           \
	\
	_type imageLoad(Texture3D<_format> _image, ivec3 _uvw)                    \
	{                                                                           \
		return _image[_uvw]._loadComponents;                                    \
	}                                                                           \
	\
	ivec3 imageSize(Texture3D<_format> _image)                                \
	{                                                                           \
		uvec3 result;                                                           \
		_image.GetDimensions(result.x, result.y, result.z);                     \
		return ivec3(result);                                                   \
	}                                                                           \
	\
	_type imageLoad(RWTexture3D<_format> _image, ivec3 _uvw)                    \
	{                                                                           \
		return _image[_uvw]._loadComponents;                                    \
	}                                                                           \
	\
	void imageStore(RWTexture3D<_format> _image, ivec3 _uvw, _type _value)      \
	{                                                                           \
		_image[_uvw] = _value._storeComponents;                                 \
	}                                                                           \
	\
	ivec3 imageSize(RWTexture3D<_format> _image)                                \
	{                                                                           \
		uvec3 result;                                                           \
		_image.GetDimensions(result.x, result.y, result.z);                     \
		return ivec3(result);                                                   \
	}

#define __IMAGE_IMPL_ATOMIC(_format, _storeComponents, _type, _loadComponents)            \
	\
	void imageAtomicAdd(RWTexture2D<_format> _image, ivec2 _uv,  _type _value)       \
	{				                                                                 \
		InterlockedAdd(_image[_uv], _value._storeComponents);	                     \
	}                                                                                \


__IMAGE_IMPL_A(float,       x,    vec4,  xxxx)
__IMAGE_IMPL_A(float2,      xy,   vec4,  xyyy)
__IMAGE_IMPL_A(float4,      xyzw, vec4,  xyzw)

__IMAGE_IMPL_A(uint,        x,    uvec4, xxxx)
__IMAGE_IMPL_A(uint2,       xy,   uvec4, xyyy)
__IMAGE_IMPL_A(uint4,       xyzw, uvec4, xyzw)

#if BGFX_SHADER_LANGUAGE_HLSL
__IMAGE_IMPL_A(unorm float,       x,    vec4,  xxxx)
__IMAGE_IMPL_A(unorm float2,      xy,   vec4,  xyyy)
__IMAGE_IMPL_A(unorm float4,      xyzw, vec4,  xyzw)
#endif

__IMAGE_IMPL_ATOMIC(uint,       x,    uvec4, xxxx)


#define atomicAdd(_mem, _data)                                       InterlockedAdd(_mem, _data)
#define atomicAnd(_mem, _data)                                       InterlockedAnd(_mem, _data)
#define atomicMax(_mem, _data)                                       InterlockedMax(_mem, _data)
#define atomicMin(_mem, _data)                                       InterlockedMin(_mem, _data)
#define atomicOr(_mem, _data)                                        InterlockedOr(_mem, _data)
#define atomicXor(_mem, _data)                                       InterlockedXor(_mem, _data)
#define atomicFetchAndAdd(_mem, _data, _original)                    InterlockedAdd(_mem, _data, _original)
#define atomicFetchAndAnd(_mem, _data, _original)                    InterlockedAnd(_mem, _data, _original)
#define atomicFetchAndMax(_mem, _data, _original)                    InterlockedMax(_mem, _data, _original)
#define atomicFetchAndMin(_mem, _data, _original)                    InterlockedMin(_mem, _data, _original)
#define atomicFetchAndOr(_mem, _data, _original)                     InterlockedOr(_mem, _data, _original)
#define atomicFetchAndXor(_mem, _data, _original)                    InterlockedXor(_mem, _data, _original)
#define atomicFetchAndExchange(_mem, _data, _original)               InterlockedExchange(_mem, _data, _original)
#define atomicFetchCompareExchange(_mem, _compare, _data, _original) InterlockedCompareExchange(_mem,_compare, _data, _original)

// InterlockedCompareStore

#define barrier()                    GroupMemoryBarrierWithGroupSync()
#define memoryBarrier()              GroupMemoryBarrierWithGroupSync()
#define memoryBarrierAtomicCounter() GroupMemoryBarrierWithGroupSync()
#define memoryBarrierBuffer()        AllMemoryBarrierWithGroupSync()
#define memoryBarrierImage()         GroupMemoryBarrierWithGroupSync()
#define memoryBarrierShared()        GroupMemoryBarrierWithGroupSync()
#define groupMemoryBarrier()         GroupMemoryBarrierWithGroupSync()

#endif // BGFX_SHADER_LANGUAGE_GLSL

#define dispatchIndirect( \
	  _buffer             \
	, _offset             \
	, _numX               \
	, _numY               \
	, _numZ               \
	)                     \
	_buffer[(_offset)*2+0] = uvec4(_numX, _numY, _numZ, 0u)

#define drawIndirect( \
	  _buffer         \
	, _offset         \
	, _numVertices    \
	, _numInstances   \
	, _startVertex    \
	, _startInstance  \
	)                 \
	_buffer[(_offset)*2+0] = uvec4(_numVertices, _numInstances, _startVertex, _startInstance)

#define drawIndexedIndirect( \
	  _buffer                \
	, _offset                \
	, _numIndices            \
	, _numInstances          \
	, _startIndex            \
	, _startVertex           \
	, _startInstance         \
	)                        \
	_buffer[(_offset)*2+0] = uvec4(_numIndices, _numInstances, _startIndex, _startVertex); \
	_buffer[(_offset)*2+1] = uvec4(_startInstance, 0u, 0u, 0u)

#endif // __cplusplus

#endif // BGFX_COMPUTE_H_HEADER_GUARD
BUFFER_RO(in_draws, vec4, 0);

BUFFER_WR(out_draws, uvec4, 1);

uniform vec4 draw_params;

NUM_THREADS(64, 1, 1)

void main()
{
    int thread_id = int(gl_GlobalInvocationID.x);
    int per_thread_draw = int(draw_params.x) / 64 + 1;
    int idx_start = thread_id * per_thread_draw;
    int idx_end = min(int(draw_params.x), (thread_id + 1) * per_thread_draw);

    for (int i = idx_start; i < idx_end; i++)
    {
        drawIndexedIndirect(out_draws, i, in_draws[i].w, 1u, in_draws[i].z, in_draws[i].x, i);
    }
}
