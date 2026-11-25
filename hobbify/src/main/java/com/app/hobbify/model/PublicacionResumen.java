package com.app.hobbify.model;

import java.time.OffsetDateTime;

public record PublicacionResumen(long id, String tipo, String titulo, OffsetDateTime publicadaEn) {}
