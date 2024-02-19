/*
 * Copyright 2011-2024 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx/blob/master/LICENSE
 */

#include <bx/uint32_t.h>
#include "common.h"
#include "bgfx_utils.h"
#include "imgui/imgui.h"
#include <iostream>

namespace
{
struct ObjIndex
{
    float vertex_start;
	float vertex_count;
	float index_start;
	float index_count;

    ObjIndex(float vs, float vc, float is, float ic)
        : vertex_start(vs), vertex_count(vc), index_start(is), index_count(ic)
    {}

    static bgfx::VertexLayout layout()
    {
        static bgfx::VertexLayout layout;
        if (layout.getStride() != 0) return layout;

        layout.begin()
            .add(bgfx::Attrib::TexCoord0, 4, bgfx::AttribType::Float)
            .end();

        return layout;
    }
};

class ExampleHelloWorld : public entry::AppI
{
public:
	ExampleHelloWorld(const char* _name, const char* _description, const char* _url)
		: entry::AppI(_name, _description, _url)
	{
	}

	bgfx::DynamicVertexBufferHandle vertex_1;
	bgfx::DynamicVertexBufferHandle vertex_2;
	bgfx::DynamicIndexBufferHandle index_1;
	bgfx::DynamicIndexBufferHandle index_2;
	bgfx::DynamicVertexBufferHandle objs_buffer;
	bgfx::IndirectBufferHandle ind1;
	bgfx::UniformHandle draw_params;
	bgfx::VertexLayout layout;
	bgfx::ProgramHandle program;
	bgfx::ProgramHandle compute;

	float vertex_buffer_1[9] =
	{
		-0.5f, -0.5f, 0.0f,
		 0.5f, -0.5f, 0.0f,
		 0.0f,  0.5f, 0.0f,
	};

	uint32_t index_buffer_1[3] =
	{
		0, 1, 2
	};

	float vertex_buffer_2[9] =
	{
		-0.5f, -0.5f, 0.0f,
		 0.0f,  0.5f, 0.0f,
		 0.5f, -0.5f, 0.0f,
	};

	uint32_t index_buffer_2[3] =
	{
		0, 2, 1
	};

	void init(int32_t _argc, const char* const* _argv, uint32_t _width, uint32_t _height) override
	{
		Args args(_argc, _argv);

		m_width  = _width;
		m_height = _height;
		m_debug  = BGFX_DEBUG_TEXT;
		m_reset  = BGFX_RESET_VSYNC;

		bgfx::Init init;
		init.type     = args.m_type;
		init.vendorId = args.m_pciId;
		init.platformData.nwh  = entry::getNativeWindowHandle(entry::kDefaultWindowHandle);
		init.platformData.ndt  = entry::getNativeDisplayHandle();
		init.platformData.type = entry::getNativeWindowHandleType();
		init.resolution.width  = m_width;
		init.resolution.height = m_height;
		init.resolution.reset  = m_reset;
		bgfx::init(init);

		// Enable debug text.
		bgfx::setDebug(m_debug);

		// Set view 0 clear state.
		bgfx::setViewClear(0
			, BGFX_CLEAR_COLOR|BGFX_CLEAR_DEPTH
			, 0x303030ff
			, 1.0f
			, 0
			);

		program = loadProgram("vs_bug", "fs_bug");

		layout.begin().add(bgfx::Attrib::Position, 3, bgfx::AttribType::Float);
		vertex_1 = bgfx::createDynamicVertexBuffer(100000, layout);
		vertex_2 = bgfx::createDynamicVertexBuffer(100000, layout);
		index_1 = bgfx::createDynamicIndexBuffer(100000, BGFX_BUFFER_INDEX32);
		index_2 = bgfx::createDynamicIndexBuffer(100000, BGFX_BUFFER_INDEX32);
		objs_buffer = bgfx::createDynamicVertexBuffer(100000, ObjIndex::layout());
		draw_params = bgfx::createUniform("draw_params", bgfx::UniformType::Vec4);
		compute = bgfx::createProgram(loadShader("cs_bug"), true);
		ind1 = bgfx::createIndirectBuffer(1);

		bgfx::update(vertex_1, 0, bgfx::makeRef(vertex_buffer_1, 9 * sizeof(float)));
		bgfx::update(vertex_2, 0, bgfx::makeRef(vertex_buffer_2, 9 * sizeof(float)));
		bgfx::update(index_1, 0, bgfx::makeRef(index_buffer_1, 3 * sizeof(float)));
		bgfx::update(index_2, 0, bgfx::makeRef(index_buffer_2, 3 * sizeof(float)));

		imguiCreate();
	}

	virtual int shutdown() override
	{
		imguiDestroy();

		// Shutdown bgfx.
		bgfx::shutdown();

		return 0;
	}

	bool update() override
	{
		if (!entry::processEvents(m_width, m_height, m_debug, m_reset, &m_mouseState) )
		{
			imguiBeginFrame(m_mouseState.m_mx
				,  m_mouseState.m_my
				, (m_mouseState.m_buttons[entry::MouseButton::Left  ] ? IMGUI_MBUT_LEFT   : 0)
				| (m_mouseState.m_buttons[entry::MouseButton::Right ] ? IMGUI_MBUT_RIGHT  : 0)
				| (m_mouseState.m_buttons[entry::MouseButton::Middle] ? IMGUI_MBUT_MIDDLE : 0)
				,  m_mouseState.m_mz
				, uint16_t(m_width)
				, uint16_t(m_height)
				);

			showExampleDialog(this);

			imguiEndFrame();

			// Set view 0 default viewport.
			bgfx::setViewRect(0, 0, 0, uint16_t(m_width), uint16_t(m_height) );

			bgfx::Encoder* encoder = bgfx::begin();
			float draw_data[4] = {1, (float) (bgfx::getDynamicIndexBufferOffset(index_2) / sizeof(uint32_t)), 0, 0};

			ObjIndex idx = ObjIndex(0, 0, 0, 3);
			bgfx::update(objs_buffer, 0, bgfx::makeRef((void*) &idx, 4 * sizeof(ObjIndex)));

			encoder->setUniform(draw_params, draw_data);
			encoder->setBuffer(0, objs_buffer, bgfx::Access::Read);
			encoder->setBuffer(1, ind1, bgfx::Access::Write);
			encoder->dispatch(0, compute, 1, 1, 1);

			encoder->setVertexBuffer(0, vertex_2);
			encoder->setIndexBuffer(index_2, 0, 3);
			encoder->submit(0, program, ind1, 0, 1);

			// Use debug font to print information about this example.
			bgfx::dbgTextClear();

			const bgfx::Stats* stats = bgfx::getStats();

			bgfx::dbgTextPrintf(0, 1, 0x0f, "Color can be changed with ANSI \x1b[9;me\x1b[10;ms\x1b[11;mc\x1b[12;ma\x1b[13;mp\x1b[14;me\x1b[0m code too.");

			bgfx::dbgTextPrintf(80, 1, 0x0f, "\x1b[;0m    \x1b[;1m    \x1b[; 2m    \x1b[; 3m    \x1b[; 4m    \x1b[; 5m    \x1b[; 6m    \x1b[; 7m    \x1b[0m");
			bgfx::dbgTextPrintf(80, 2, 0x0f, "\x1b[;8m    \x1b[;9m    \x1b[;10m    \x1b[;11m    \x1b[;12m    \x1b[;13m    \x1b[;14m    \x1b[;15m    \x1b[0m");

			bgfx::dbgTextPrintf(0, 2, 0x0f, "Backbuffer %dW x %dH in pixels, debug text %dW x %dH in characters."
				, stats->width
				, stats->height
				, stats->textWidth
				, stats->textHeight
				);

			// Advance to next frame. Rendering thread will be kicked to
			// process submitted rendering primitives.
			bgfx::frame();

			return true;
		}

		return false;
	}

	entry::MouseState m_mouseState;

	uint32_t m_width;
	uint32_t m_height;
	uint32_t m_debug;
	uint32_t m_reset;
};

} // namespace

ENTRY_IMPLEMENT_MAIN(
	  ExampleHelloWorld
	, "01-bug"
	, "Initialization and debug text."
	, "https://bkaradzic.github.io/bgfx/examples.html#helloworld"
	);
